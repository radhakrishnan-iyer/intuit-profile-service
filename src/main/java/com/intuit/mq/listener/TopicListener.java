package com.intuit.mq.listener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intuit.common.model.mq.EventReply;
import com.intuit.processor.IResponseProcessor;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TopicListener implements DeliverCallback {
    private static Logger logger = LoggerFactory.getLogger(TopicListener.class);

    private final IResponseProcessor responseProcessor;
    private final ExecutorService executorService;

    public TopicListener(IResponseProcessor requestProcessor) {
        this(requestProcessor , 20);
    }

    public TopicListener(IResponseProcessor responseProcessor , int threadPoolCount) {
        this.responseProcessor = responseProcessor;
        executorService = Executors.newFixedThreadPool(threadPoolCount , new ThreadFactoryBuilder().setNameFormat("Response-Processor-%d").build());
    }

    @Override
    public void handle(String consumerTag, Delivery message) throws IOException {
        Object object = SerializationUtils.deserialize(message.getBody());
        if(object instanceof EventReply) {
            EventReply eventReply = (EventReply) object;
            executorService.execute(() -> responseProcessor.process(eventReply));
        } else {
            logger.error("Ignoring the message from topic as its type does not match the expected type");
        }
    }
}
