package redisjedisdemo.jedisdemo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 通过@ConfigurationProperties进行application.yml文件的自动配置
 * 此外还需添加@Component注解将此配置类声明为Bean供其他类自动注入
 */
@Component
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedisProperties {

    private String host;

    private String password;

    private int port;

    private final Jedis jedis = new Jedis();

    @Getter
    @Setter
    public static class Jedis {
        private Pool pool;
    }

    @Getter
    @Setter
    public static class Pool {
        private int maxActive;
        private int maxIdle;
        private int minIdle;
        private Duration maxWait;
    }

}
