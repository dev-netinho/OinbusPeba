package br.edu.oinbupebas.transit.application;

import br.edu.oinbupebas.config.OptimisticConflictException;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransitAdminService {
  private final JdbcClient jdbc;
  TransitAdminService(JdbcClient jdbc){this.jdbc=jdbc;}
  @Transactional(readOnly=true) public List<AdminLine> lines(){return jdbc.sql("select id,short_name,name,color,version from transit_line order by short_name")
    .query((rs,n)->new AdminLine(rs.getString("id"),rs.getString("short_name"),rs.getString("name"),rs.getString("color"),rs.getLong("version"))).list();}
  @Transactional public AdminLine updateLine(String id,String name,String color,long expectedVersion,String actor){
    int changed=jdbc.sql("update transit_line set name=:name,color=:color,version=version+1 where id=:id and version=:version")
      .param("name",name).param("color",color).param("id",id).param("version",expectedVersion).update();
    if(changed==0)throw new OptimisticConflictException("A linha foi alterada por outra sessão. Recarregue antes de salvar.");
    jdbc.sql("insert into operations_audit(actor,action,resource,details,occurred_at) values(:actor,'LINE_UPDATED',:resource,jsonb_build_object('previousVersion',:version),:at)")
      .param("actor",actor).param("resource","transit_line/"+id).param("version",expectedVersion).param("at",Instant.now()).update();
    return jdbc.sql("select id,short_name,name,color,version from transit_line where id=:id").param("id",id)
      .query((rs,n)->new AdminLine(rs.getString("id"),rs.getString("short_name"),rs.getString("name"),rs.getString("color"),rs.getLong("version"))).single();
  }
  public record AdminLine(String id,String shortName,String name,String color,long version){}
}
