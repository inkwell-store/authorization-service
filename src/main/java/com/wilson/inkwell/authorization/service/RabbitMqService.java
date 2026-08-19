package com.wilson.inkwell.authorization.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.wilson.inkwell.authorization.dto.CredentialCreationEvent;

@Service
public class RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCredentialCreationMessage(CredentialCreationEvent event) {
        rabbitTemplate.convertAndSend("credential.events", null, event);
    }

}
