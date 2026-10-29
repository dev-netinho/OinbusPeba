package br.edu.oinbupebas.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration @EnableScheduling
public class MessagingConfig {
  public static final String EVENTS="oinbupebas.events",DLX="oinbupebas.dlx",IMPORT_QUEUE="oinbupebas.catalog-import",DLQ="oinbupebas.dlq";
  @Bean TopicExchange events(){return new TopicExchange(EVENTS,true,false);}
  @Bean DirectExchange dlx(){return new DirectExchange(DLX,true,false);}
  @Bean Queue imports(){return QueueBuilder.durable(IMPORT_QUEUE).withArgument("x-dead-letter-exchange",DLX).withArgument("x-dead-letter-routing-key","catalog.import.failed").build();}
  @Bean Binding importBinding(Queue imports,TopicExchange events){return BindingBuilder.bind(imports).to(events).with("catalog.import.requested");}
  @Bean Queue dlq(){return QueueBuilder.durable(DLQ).build();}
  @Bean Binding dlqBinding(Queue dlq,DirectExchange dlx){return BindingBuilder.bind(dlq).to(dlx).with("catalog.import.failed");}
  @Bean Advice importRetry(RabbitTemplate rabbit){
    var recoverer=new RepublishMessageRecoverer(rabbit,DLX,"catalog.import.failed");
    return RetryInterceptorBuilder.stateless().maxAttempts(3).backOffOptions(500,2,3000).recoverer(recoverer).build();
  }
  @Bean SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer,ConnectionFactory connectionFactory,
      @Qualifier("importRetry") Advice advice){
    var factory=new SimpleRabbitListenerContainerFactory();configurer.configure(factory,connectionFactory);factory.setAdviceChain(advice);return factory;
  }
}
