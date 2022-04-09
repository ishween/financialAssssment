package com.example.financialassessment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

//    public final static String QUEUE = "financialAssessment_Queue", EXCHANGE = "financialAssessment_Exchange", ROUTING_KEY = "financialAssessment_RoutingKey";
//    public final static String QUEUE = System.getenv("QUEUE"), EXCHANGE = System.getenv("EXCHANGE"), ROUTING_KEY = System.getenv("ROUTING_KEY");
    @Value("${QUEUE}")
    public String QUEUE;

    @Value("${EXCHANGE}")
    public String EXCHANGE;

    @Value("${ROUTING_KEY}")
    public String ROUTING_KEY;

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
}
