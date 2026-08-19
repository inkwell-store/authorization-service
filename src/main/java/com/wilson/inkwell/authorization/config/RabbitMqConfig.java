package com.wilson.inkwell.authorization.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {

    // Declaring Exchange names
    private final String CREDENTIAL_EXCHANGE = "credential.events";

    @Bean
    CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");

        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setChannelCacheSize(25);

        return connectionFactory;
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    FanoutExchange credentialFanoutExchange() {
        return new FanoutExchange(CREDENTIAL_EXCHANGE);
    }

    @Bean
    Queue customerQueue() {
        return new Queue("customer.credential-queue");
    }

    @Bean
    Queue notificationQueue() {
        return new Queue("notification.credential-queue");
    }

    @Bean
    Binding customerBinding() {
        return BindingBuilder.bind(customerQueue()).to(credentialFanoutExchange());
    }

    @Bean
    Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(credentialFanoutExchange());
    }
}
