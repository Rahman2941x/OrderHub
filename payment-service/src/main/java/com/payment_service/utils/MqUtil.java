package com.payment_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqUtil {


    private static final Logger log = LoggerFactory.getLogger(MqUtil.class);
    @Autowired
    Utils utils;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public <T> Object sendAndReceive(String queue, T request) throws JsonProcessingException {
        log.info("Request being send sync to this queue {} is {}", queue, request);

        Object response = rabbitTemplate.convertSendAndReceive(queue, utils.convertObjectToJson(request));

        log.info("Response being received sync to this queue {} is {}", queue, response);
        return response;
    }

    public <T> void send(String queue, T request) throws JsonProcessingException {
        log.info("Request being send to this queue {} is {}", queue, request);
        rabbitTemplate.convertAndSend(queue, utils.convertObjectToJson(request));
    }
}
