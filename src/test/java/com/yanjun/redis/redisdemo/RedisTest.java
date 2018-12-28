package com.yanjun.redis.redisdemo;

import com.yanjun.redis.redisdemo.model.User;
import com.yanjun.redis.redisdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;



    @Test
    public void testOpsForValue() {
        String key = "user:1";
        redisTemplate.opsForValue().set(key, new User(1L, "Jun", 27));
        User user = (User) redisTemplate.opsForValue().get(key);
        log.info("user: " + user.toString());
        log.info("user name: " + user.getName() + ", user age: " + user.getAge());

        key = "user:2";
        redisTemplate.opsForValue().set(key, new User(2L, "Ken", 26));
    }

    @Test
    public void testOpsForValueExp() {
        String key = "user:3";
        redisTemplate.opsForValue().set(key, new User(3L, "Bob", 18), 10, TimeUnit.SECONDS);
        User user = (User) redisTemplate.opsForValue().get(key);
        log.info(user.toString());
    }

    @Test
    public void testOpsForValueMul() {
        Map<String, String> map = new HashMap<>();
        map.put("name1", "hello");
        map.put("name2", "world");
        map.put("name3", "happy");
        redisTemplate.opsForValue().multiSet(map);

        List<String> keys = new ArrayList<>();
        keys.add("name1");
        keys.add("name2");
        keys.add("name3");
        List res = redisTemplate.opsForValue().multiGet(keys);
        log.info(res.toString());
    }

    @Test
    public void testOpsForValueIcr() {
        redisTemplate.opsForValue().increment("incrNumber", 4);
        redisTemplate.opsForValue().increment("incrDouble", 3.1);
    }

    @Test
    public void testOpsForList() {
        redisTemplate.opsForList().leftPushAll("list", "Java", "Python", "C", "JS");
        List list = redisTemplate.opsForList().range("list", 0, -1);
        log.info("list: {}", list.size());
    }


    @Test
    public void testOpsForHash() {
        String key = "friends:1";
        User u1 = new User(2L, "Bob", 18);
        User u2 = new User(3L, "Mike", 20);
        User u3 = new User(4L, "Jack", 24);

        Map<String, User> map = new HashMap<>();
        map.put("u1", u1);
        map.put("u2", u2);
        map.put("u3", u3);

        redisTemplate.opsForHash().putAll(key, map);

    }

    @Test
    public void testUserAllCache() {
        List<User> result = userService.getUsers();
        for (User user : result) {
            log.info("user name is : {}", user.getName());
        }
    }

    @Test
    public void testOneUserCache() {
        User user = userService.getUserById(11L);
        log.info("user name is : {}", user.getName());
    }


}
