package com.github.paicoding.forum.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.paicoding.forum.core.cache.RedisClient;
import com.github.paicoding.forum.core.config.ProxyProperties;
import com.github.paicoding.forum.core.net.ProxyCenter;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author YiHui
 * @date 2022/9/4
 */
@Configuration
@EnableConfigurationProperties(ProxyProperties.class)
@ComponentScan(basePackages = "com.github.paicoding.forum.core")
public class ForumCoreAutoConfig {
    @Autowired
    private ProxyProperties proxyProperties;

    public ForumCoreAutoConfig(RedisTemplate<String, String> redisTemplate) {
        RedisClient.register(redisTemplate);
    }

    /**
     * 定义缓存管理器，配合Spring的 @Cache 来使用
     *
     * @return
     */
    @Bean("caffeineCacheManager")
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder().
                // 设置过期时间，写入后五分钟过期
                        expireAfterWrite(5, TimeUnit.MINUTES)
                // 初始化缓存空间大小
                .initialCapacity(100)
                // 最大的缓存条数
                .maximumSize(200)
        );
        return cacheManager;
    }

    @PostConstruct
    public void init() {
        // 这里借助手动解析配置信息，并实例化为Java POJO对象，来实现代理池的初始化
        ProxyCenter.initProxyPool(proxyProperties.getProxy());
    }


    /**
     * 如果多种缓存技术结合，比如使用redis情况下，为防止缓存雪崩，需要按不同业务赋予不同的过期时间配置
     * 可以采用以下方法添加
     * @param factory
     * @return
     */
    /*
    @Bean(name = CacheManagerNames.REDIS_CACHE_MANAGER)
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        Map<String, RedisCacheConfiguration> expires = ImmutableMap.<String, RedisCacheConfiguration>builder()
                .put("15sCache", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(15000)))
                .put("30sCache", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(30000)))
                .put("60sCache", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(60000)))
                .put("120sCache", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMillis(120000)))
                .build();
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(factory) .withInitialCacheConfigurations(expires)
                .build();
    }

     */
}
