package com.intuit.config;

import com.intuit.cache.ICacheService;
import com.intuit.cache.InMemoryCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public ICacheService cacheService() {
        return new InMemoryCacheService();
    }
}
