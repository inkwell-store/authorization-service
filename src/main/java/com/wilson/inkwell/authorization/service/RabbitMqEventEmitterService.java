package com.wilson.inkwell.authorization.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.wilson.inkwell.authorization.dto.CredentialCreationEvent;

@Service
public class RabbitMqEventEmitterService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqEventEmitterService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCredentialCreationMessage(CredentialCreationEvent event) {
        rabbitTemplate.convertAndSend("credential.events", null, event);
    }

}
