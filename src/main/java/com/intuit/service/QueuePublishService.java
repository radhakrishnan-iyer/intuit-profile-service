package com.intuit.service;

import com.intuit.common.constant.Constants;
import com.intuit.common.model.Request;
import com.intuit.common.model.mq.Event;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;

public class QueuePublishService {

    private static Logger logger = LoggerFactory.getLogger(QueuePublishService.class);

    private final Channel channel;
    private final String queueName;

    public QueuePublishService(Channel channel, String queueName) {
        this.channel = channel;
        this.queueName = queueName;
    }

    public boolean publish(Request request) {
        Event event = new Event();
        event.setCorrelationId(MDC.get(Constants.request_id));
        event.setCustomerId(MDC.get(Constants.customer_id));
        event.setRequest(request);

        try {
            channel.queueDeclare(queueName, false, false, false, null);
            byte[] data = SerializationUtils.serialize(event);
            channel.basicPublish("", queueName, null, data);
            logger.info("Request id {} published on the new_request_queue" , event.getCorrelationId());
            return true;
        }
        catch (IOException ex) {
            logger.error("Exception while pushing the message on new_request_queue" , ex);
            throw new RuntimeException(ex);
        }
    }
}
