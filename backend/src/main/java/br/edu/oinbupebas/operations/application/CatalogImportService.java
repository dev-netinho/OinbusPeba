package br.edu.oinbupebas.operations.application;

import br.edu.oinbupebas.operations.domain.OutboxEvent;
import br.edu.oinbupebas.operations.domain.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.csv.CSVFormat;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CatalogImportService {
  private static final UUID DEMO_OPERATOR=UUID.fromString("00000000-0000-0000-0000-000000000001");
  private final JdbcClient jdbc; private final OutboxEventRepository outbox; private final ObjectMapper json; private final TransactionTemplate transactions;
  CatalogImportService(JdbcClient jdbc,OutboxEventRepository outbox,ObjectMapper json,TransactionTemplate transactions){this.jdbc=jdbc;this.outbox=outbox;this.json=json;this.transactions=transactions;}

  public ImportTask create(MultipartFile file) throws IOException {
    if(file.isEmpty())throw new IllegalArgumentException("O arquivo CSV está vazio.");
    if(file.getSize()>2_000_000)throw new IllegalArgumentException("Arquivo excede 2 MB.");
    var original=file.getOriginalFilename()==null?"catalogo.csv":file.getOriginalFilename();
    if(!original.toLowerCase(Locale.ROOT).endsWith(".csv"))throw new IllegalArgumentException("Envie um arquivo CSV.");
    var filename=original.replaceAll("[^A-Za-z0-9._-]","_"); var content=file.getBytes();var id=UUID.randomUUID();var now=Instant.now();
    return transactions.execute(status->{
      jdbc.sql("insert into operations_import_task(id,filename,content,status,created_at,updated_at) values(:id,:filename,:content,'PENDING',:now,:now)")
        .param("id",id).param("filename",filename).param("content",content).param("now",now).update();
      outbox.save(new OutboxEvent("catalog.import.requested",write(Map.of("taskId",id.toString()))));
      return new ImportTask(id,filename,"PENDING",List.of(),now,null,0);
    });
  }

  public void process(UUID eventId,UUID taskId){
    if(processed(eventId))return;
    byte[] content=jdbc.sql("select content from operations_import_task where id=:id").param("id",taskId).query(byte[].class).optional()
      .orElseThrow(()->new IllegalStateException("Tarefa de importação inexistente: "+taskId));
    var validation=validate(content);
    transactions.executeWithoutResult(status->{
      if(processed(eventId))return;
      jdbc.sql("delete from operations_import_stage where task_id=:id").param("id",taskId).update();
      for(var row:validation.rows())jdbc.sql("insert into operations_import_stage(task_id,row_number,record_type,payload) values(:task,:row,:type,cast(:payload as jsonb))")
        .param("task",taskId).param("row",row.number()).param("type",row.type()).param("payload",write(row.payload())).update();
      String state=validation.errors().isEmpty()?"READY":"FAILED";
      jdbc.sql("update operations_import_task set status=:status,error_report=cast(:errors as jsonb),updated_at=:at,version=version+1 where id=:id")
        .param("status",state).param("errors",write(validation.errors())).param("at",Instant.now()).param("id",taskId).update();
      jdbc.sql("insert into operations_processed_event(consumer,event_id,processed_at) values('catalog-import',:event,:at) on conflict do nothing")
        .param("event",eventId).param("at",Instant.now()).update();
    });
  }

  public ImportTask publish(UUID taskId,long expectedVersion,String actor){
    return transactions.execute(status->{
      var task=find(taskId); if(!"READY".equals(task.status()))throw new IllegalArgumentException("Somente importações validadas podem ser publicadas.");
      if(task.version()!=expectedVersion)throw new br.edu.oinbupebas.config.OptimisticConflictException("A tarefa foi alterada por outra sessão.");
      var stages=jdbc.sql("select record_type,payload::text from operations_import_stage where task_id=:id order by row_number").param("id",taskId)
        .query((rs,n)->new Stage(rs.getString(1),readMap(rs.getString(2)))).list();
      for(var stage:stages)publish(stage);
      int changed=jdbc.sql("update operations_import_task set status='PUBLISHED',published_at=:at,updated_at=:at,version=version+1 where id=:id and version=:version and status='READY'")
        .param("at",Instant.now()).param("id",taskId).param("version",expectedVersion).update();
      if(changed==0)throw new br.edu.oinbupebas.config.OptimisticConflictException("A tarefa foi alterada por outra sessão.");
      audit(actor,"CATALOG_IMPORT_PUBLISHED","operations_import_task/"+taskId);
      return find(taskId);
    });
  }

  public ImportTask reprocess(UUID taskId,String actor){
    return transactions.execute(status->{var task=find(taskId);if(!"FAILED".equals(task.status()))throw new IllegalArgumentException("Somente tarefas com falha podem ser reprocessadas.");
      jdbc.sql("update operations_import_task set status='PENDING',error_report='[]',updated_at=:at,version=version+1 where id=:id").param("at",Instant.now()).param("id",taskId).update();
      outbox.save(new OutboxEvent("catalog.import.requested",write(Map.of("taskId",taskId.toString()))));audit(actor,"CATALOG_IMPORT_REQUEUED","operations_import_task/"+taskId);return find(taskId);});
  }
  public List<ImportTask> list(){return jdbc.sql("select id,filename,status,error_report::text,created_at,published_at,version from operations_import_task order by created_at desc limit 100").query((rs,n)->new ImportTask(rs.getObject(1,UUID.class),rs.getString(2),rs.getString(3),readList(rs.getString(4)),rs.getTimestamp(5).toInstant(),rs.getTimestamp(6)==null?null:rs.getTimestamp(6).toInstant(),rs.getLong(7))).list();}
  public ImportTask find(UUID id){return jdbc.sql("select id,filename,status,error_report::text,created_at,published_at,version from operations_import_task where id=:id").param("id",id).query((rs,n)->new ImportTask(rs.getObject(1,UUID.class),rs.getString(2),rs.getString(3),readList(rs.getString(4)),rs.getTimestamp(5).toInstant(),rs.getTimestamp(6)==null?null:rs.getTimestamp(6).toInstant(),rs.getLong(7))).optional().orElseThrow(()->new IllegalArgumentException("Tarefa não encontrada."));}

  static Validation validate(byte[] content){
    var rows=new ArrayList<ValidatedRow>();var errors=new ArrayList<ImportError>();
    try(var reader=new InputStreamReader(new ByteArrayInputStream(content),StandardCharsets.UTF_8)){
      var records=CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).get().parse(reader);
      var headers=records.getHeaderNames();
      if(!headers.containsAll(List.of("type","id","name","latitude","longitude","short_name","color","provenance")))return new Validation(List.of(),List.of(new ImportError(1,"Cabeçalho obrigatório: type,id,name,latitude,longitude,short_name,color,provenance")));
      for(var record:records){int number=(int)record.getRecordNumber()+1;try{
        String type=record.get("type").toUpperCase(Locale.ROOT),id=required(record.get("id"),"id"),name=required(record.get("name"),"name");
        if(!id.matches("[a-z0-9-]{2,80}"))throw new IllegalArgumentException("id deve usar letras minúsculas, números e hífen");
        if("STOP".equals(type)){double lat=Double.parseDouble(record.get("latitude")),lon=Double.parseDouble(record.get("longitude"));if(lat < -90||lat>90||lon < -180||lon>180)throw new IllegalArgumentException("coordenadas fora do intervalo");rows.add(new ValidatedRow(number,type,Map.of("id",id,"name",name,"latitude",lat,"longitude",lon,"provenance",required(record.get("provenance"),"provenance"))));}
        else if("LINE".equals(type)){String color=record.get("color"),shortName=required(record.get("short_name"),"short_name");if(!color.matches("#[0-9A-Fa-f]{6}"))throw new IllegalArgumentException("color deve usar #RRGGBB");rows.add(new ValidatedRow(number,type,Map.of("id",id,"name",name,"shortName",shortName,"color",color.toUpperCase(Locale.ROOT),"provenance",required(record.get("provenance"),"provenance"))));}
        else throw new IllegalArgumentException("type deve ser STOP ou LINE");
      }catch(RuntimeException ex){errors.add(new ImportError(number,ex.getMessage()));}}
    }catch(IOException ex){errors.add(new ImportError(1,"CSV ilegível: "+ex.getMessage()));}
    if(rows.isEmpty()&&errors.isEmpty())errors.add(new ImportError(2,"O CSV não contém registros."));return new Validation(List.copyOf(rows),List.copyOf(errors));
  }
  private void publish(Stage stage){
    var p=stage.payload();
    if("STOP".equals(stage.type())){
      jdbc.sql("insert into transit_stop(id,name,location,provenance) values(:id,:name,ST_SetSRID(ST_MakePoint(:lon,:lat),4326)::geography,jsonb_build_object('descricao',cast(:provenance as text))) "
          +"on conflict(id) do update set name=excluded.name,location=excluded.location,provenance=excluded.provenance,version=transit_stop.version+1")
        .param("id",p.get("id")).param("name",p.get("name")).param("lon",p.get("longitude")).param("lat",p.get("latitude")).param("provenance",p.get("provenance")).update();
    }else{
      jdbc.sql("insert into transit_line(id,operator_id,short_name,name,color,provenance) values(:id,:operator,:shortName,:name,:color,jsonb_build_object('descricao',cast(:provenance as text))) "
          +"on conflict(id) do update set short_name=excluded.short_name,name=excluded.name,color=excluded.color,provenance=excluded.provenance,version=transit_line.version+1")
        .param("id",p.get("id")).param("operator",DEMO_OPERATOR).param("shortName",p.get("shortName")).param("name",p.get("name")).param("color",p.get("color")).param("provenance",p.get("provenance")).update();
    }
  }
  private boolean processed(UUID event){return jdbc.sql("select count(*) from operations_processed_event where consumer='catalog-import' and event_id=:event").param("event",event).query(Integer.class).single()>0;}
  private void audit(String actor,String action,String resource){jdbc.sql("insert into operations_audit(actor,action,resource) values(:actor,:action,:resource)").param("actor",actor).param("action",action).param("resource",resource).update();}
  private static String required(String value,String field){if(value==null||value.isBlank())throw new IllegalArgumentException(field+" é obrigatório");return value.trim();}
  private String write(Object value){try{return json.writeValueAsString(value);}catch(JsonProcessingException e){throw new IllegalStateException(e);}}
  @SuppressWarnings("unchecked") private Map<String,Object> readMap(String value){try{return json.readValue(value,Map.class);}catch(JsonProcessingException e){throw new IllegalStateException(e);}}
  @SuppressWarnings("unchecked") private List<ImportError> readList(String value){try{return json.readerForListOf(ImportError.class).readValue(value);}catch(JsonProcessingException e){throw new IllegalStateException(e);}}
  public record ImportTask(UUID id,String filename,String status,List<ImportError> errors,Instant createdAt,Instant publishedAt,long version){}
  public record ImportError(int row,String message){}
  record ValidatedRow(int number,String type,Map<String,Object> payload){} record Validation(List<ValidatedRow> rows,List<ImportError> errors){} private record Stage(String type,Map<String,Object> payload){}
}
