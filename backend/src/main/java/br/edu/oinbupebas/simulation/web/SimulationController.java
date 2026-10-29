package br.edu.oinbupebas.simulation.web;
import br.edu.oinbupebas.simulation.application.SimulationService; import java.io.IOException; import java.util.concurrent.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RestController @RequestMapping("/api/simulation") public class SimulationController {
 private final SimulationService service; private final ScheduledExecutorService scheduler=Executors.newScheduledThreadPool(2,Thread.ofVirtual().factory()); SimulationController(SimulationService s){service=s;}
 @GetMapping("/vehicles") Object positions(){return service.positions();}
 @GetMapping("/stream") SseEmitter stream(){var emitter=new SseEmitter(120_000L);var task=scheduler.scheduleAtFixedRate(()->{try{emitter.send(SseEmitter.event().name("vehicles").data(service.positions()));}catch(IOException e){emitter.complete();}},0,4,TimeUnit.SECONDS);emitter.onCompletion(()->task.cancel(true));emitter.onTimeout(()->task.cancel(true));emitter.onError(e->task.cancel(true));return emitter;}
}
