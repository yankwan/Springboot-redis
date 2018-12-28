package com.yanjun.redis.redisdemo;

import com.yanjun.redis.redisdemo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StringTmpRedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testOpsForValue() {
        String key = "user-string:1";
        stringRedisTemplate.opsForValue().set(key, new User(1L, "Jun", 27).toString());
        String str = stringRedisTemplate.opsForValue().get(key);
        log.info("user: " + str);

        key = "user-string:2";
        stringRedisTemplate.opsForValue().set(key, new User(2L, "Ken", 26).toString());
    }

}
