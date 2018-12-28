# Springboot-redis

Springboot2.x整合redis

#### 配置

* application.yml 配置文件

2.x版本中最大的变化是将java操作redis客户端的jedis替换成lettuce。

lettuce基于Netty连接，可以在多个线程中并发访问，属于线程安全的。因此一个连接实例就可以满足多线程下并发访问。

```yaml
spring:
  redis:
      host: 127.0.0.1
      port: 6379
      lettuce:
          pool:
              max-active: 100
              max-idle: 10
              max-wait: -1ms

```

* RedisConfig 配置类

配置RedisTemplate模板的序列化方式。序列化方式有：

1. Jackson2JsonRedisSerializer
2. GenericJackson2JsonRedisSerializer
3. stringSerializer

RedisTemplate默认序列化类是JdkSerializationRedisSerializer，不便于存储数据的阅读，一般通过序列化成json后存储。

```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
{
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jackson2JsonRedisSerializer.setObjectMapper(om);

    RedisSerializer stringSerializer = new StringRedisSerializer();

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(jackson2JsonRedisSerializer);
    template.afterPropertiesSet();
    return template;
}
```


CacheManager默认采用JDK的序列化方式，同样造成可视化工具中存在不具备阅读性问题。

这里通过FastJsonRedisSerializer进行序列化。

```java
@Bean
public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    // 初始化RedisCacheWriter
    RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

    // 设置缓存序列化方式
    ClassLoader loader = this.getClass().getClassLoader();
    FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer<>(loader.getClass());
    RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer);
    RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);

    // RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
    // 设置默认超过期时间是1天
    defaultCacheConfig.entryTtl(Duration.ofDays(1));
    // 初始化RedisCacheManager
    RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    return cacheManager;
}
```