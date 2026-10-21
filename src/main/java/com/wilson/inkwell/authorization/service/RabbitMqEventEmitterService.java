package com.wilson.inkwell.authorization.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.wilson.inkwell.authorization.config.RabbitExchangeNames;
import com.wilson.inkwell.authorization.dto.CredentialCreationEvent;

/**
 * Service responsible for communicating with the RabbitMQ service. It's responsible
 * for sending messages, making it a <code>producer</code>.
 */
@Service
public class RabbitMqEventEmitterService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqEventEmitterService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCredentialCreationMessage(CredentialCreationEvent event) {
        rabbitTemplate.convertAndSend(RabbitExchangeNames.CREDENTIAL_EXCHANGE, null, event);
    }

}
