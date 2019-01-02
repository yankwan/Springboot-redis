package redisjedisdemo.jedisdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void redisTemplateTest() {
        redisTemplate.opsForValue().set("testKey", "testValue");
        String s = (String)redisTemplate.opsForValue().get("testKey");
        log.info("String is : {}", s);
    }
}
