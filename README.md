# Springboot-redis

Springboot2.x整合redis

#### 配置

* application.yml 配置文件

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
@Configuration
public class RedisConfig {

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

}
```