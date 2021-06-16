package com.intuit.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intuit.cache.CacheManager;
import com.intuit.cache.ICacheService;
import com.intuit.common.config.SwaggerConfig;
import com.intuit.common.config.WebConfig;
import com.intuit.dao.IProfileDao;
import com.intuit.dao.ITransactionDao;
import com.intuit.mq.listener.TopicListener;
import com.intuit.mq.listener.TopicSubscriptionService;
import com.intuit.service.QueuePublishService;
import com.intuit.processor.IRequestProcessor;
import com.intuit.processor.IResponseProcessor;
import com.intuit.processor.RequestProcessor;
import com.intuit.processor.ResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Import({WebConfig.class,MqConfig.class,DBConfig.class,CacheConfig.class,SwaggerConfig.class})
public class AppConfig {

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private ITransactionDao transactionDao;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IProfileDao profileDao;

    @Value("${response.processor.thread.pool.size:20}")
    private int requestProcessorThreadPoolSize;

    @Value("${request.processor.thread.poll.size:100}")
    private int requestProcessorThreadPollSize;

    @Value("${transaction.insert.query}")
    private String insertTransaction;

    @Value("${get.transaction.query}")
    private String getTransactionQuery;

    @Value("${profile.upsert.query}")
    private String upsertProfile;

    @Bean
    public IResponseProcessor responseProcessor() {
        ResponseProcessor requestProcessor = new ResponseProcessor(transactionDao , profileDao, cacheManager() , insertTransaction , upsertProfile);
        return requestProcessor;
    }

    @Bean
    public TopicListener topicListener() {
        TopicListener topicListener = new TopicListener(responseProcessor() , requestProcessorThreadPoolSize);
        return topicListener;
    }

    @Bean(initMethod = "init")
    public TopicSubscriptionService topicSubscriptionService() throws Exception {
        TopicSubscriptionService topicSubscriptionService = new TopicSubscriptionService(mqConfig.topicChannel() ,
                mqConfig.getReplyTopic(), mqConfig.getRoutingKeyPrefix(), topicListener());
        return topicSubscriptionService;
    }

    @Bean
    public QueuePublishService publishService() throws Exception{
        QueuePublishService queuePublishService = new QueuePublishService(mqConfig.queueChannel() , mqConfig.getQueueName());
        return queuePublishService;
    }

    @Bean
    public IRequestProcessor requestProcessor() throws Exception {
        RequestProcessor requestProceesor = new RequestProcessor(publishService() , transactionDao , profileDao, cacheManager(), insertTransaction, getTransactionQuery);
        return requestProceesor;
    }

    @Bean
    public ExecutorService requestExecutorService() {
        return Executors.newFixedThreadPool(requestProcessorThreadPollSize ,
                new ThreadFactoryBuilder().setNameFormat("Request-Processor-%d").build());
    }

    @Bean(initMethod = "loadCache")
    public CacheManager cacheManager() {
        CacheManager cacheManager = new CacheManager(cacheService , profileDao);
        return cacheManager;
    }
}