package com.example.financialassessment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@EnableRabbit
//@Configuration
@Configuration
public class MessagingConfig {
//    public final static String QUEUE = "financialAssessment_Queue", EXCHANGE = "financialAssessment_Exchange", ROUTING_KEY = "financialAssessment_RoutingKey";
//    public final static String QUEUE = System.getenv("QUEUE"), EXCHANGE = System.getenv("EXCHANGE"), ROUTING_KEY = System.getenv("ROUTING_KEY");
//    @Value("${QUEUE}")
//    public String QUEUE;
//
//    @Value("${EXCHANGE}")
//    public String EXCHANGE;
//
//    @Value("${ROUTING_KEY}")
//    public String ROUTING_KEY;

    @Value("${rabbitmq.queue}")
    private String QUEUE;
    @Value("${rabbitmq.exchange}")
    private String EXCHANGE;
    @Value("${rabbitmq.routingkey}")
    private String ROUTING_KEY;
//    @Value("${rabbitmq.username}")
//    private String username;
//    @Value("${rabbitmq.password}")
//    private String password;
//    @Value("${rabbitmq.host}")
//    private String host;
//    @Value("${rabbitmq.virtualhost}")
//    private String virtualHost;
//    @Value("${rabbitmq.reply.timeout}")
//    private Integer replyTimeout;
//    @Value("${rabbitmq.concurrent.consumers}")
//    private Integer concurrentConsumers;
//    @Value("${rabbitmq.max.concurrent.consumers}")
//    private Integer maxConcurrentConsumers;

//    public MessagingConfig(){
//        QUEUE = System.getenv("QUEUE");
//        EXCHANGE = System.getenv("EXCHANGE");
//        ROUTING_KEY = System.getenv("ROUTING_KEY");
//    }

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding bind(){
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }

//    @Bean
//    public ConnectionFactory connectionFactory() {
//        System.out.println("Trying to connect");
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        System.out.println("Trying to connect 1");
//        connectionFactory.setVirtualHost(virtualHost);
//        connectionFactory.setHost(host);
//        connectionFactory.setUsername(username);
//        connectionFactory.setPassword(password);
//        System.out.println("Trying to connect 2");
//        return connectionFactory;
//    }

//    @Bean
//    public AmqpAdmin amqpAdmin() {
//        return new RabbitAdmin(connectionFactory());
//    }
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
//        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory());
//        factory.setMessageConverter(converter());
//        factory.setConcurrentConsumers(concurrentConsumers);
//        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
//        factory.setErrorHandler(errorHandler());
//        return factory;
//    }
//    @Bean
//    public ErrorHandler errorHandler() {
//        return new ConditionalRejectingErrorHandler(new MyFatalExceptionStrategy());
//    }
//    public static class MyFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {
////        private final Logger logger = LogManager.getLogger(getClass());
//        @Override
//        public boolean isFatal(Throwable t) {
//            if (t instanceof ListenerExecutionFailedException) {
//                ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) t;
//                logger.error("Failed to process inbound message from queue "
//                        + lefe.getFailedMessage().getMessageProperties().getConsumerQueue()
//                        + "; failed message: " + lefe.getFailedMessage(), t);
//            }
//            return super.isFatal(t);
//        }
//    }
}
