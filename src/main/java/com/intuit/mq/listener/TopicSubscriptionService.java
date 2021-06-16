package com.intuit.mq.listener;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicSubscriptionService {
    private static Logger logger = LoggerFactory.getLogger(TopicSubscriptionService.class);

    private final Channel channel;
    private final String topicName;
    private final String routingKeyPrefix;
    private final TopicListener topicListener;

    public TopicSubscriptionService(Channel channal, String topicName, String routingKeyPrefix, TopicListener topicListener) {
        this.channel = channal;
        this.topicName = topicName;
        this.routingKeyPrefix = routingKeyPrefix;
        this.topicListener = topicListener;
    }

    public void init() throws Exception {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, topicName, routingKeyPrefix);
        channel.basicConsume(queueName, true, topicListener, consumerTag -> { });
    }
}
