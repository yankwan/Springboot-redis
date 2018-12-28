package com.yanjun.redis.redisdemo.service;

import com.yanjun.redis.redisdemo.model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "user")
public class UserService implements Serializable {

    @Cacheable
    public List<User> getUsers() {
        User u1 = new User(1L, "Bob_" + System.currentTimeMillis(), 21);
        User u2 = new User(2L, "Mike_" + System.currentTimeMillis(), 22);
        User u3 = new User(3L, "Jack_" + System.currentTimeMillis(), 23);
        List list = new ArrayList() {{
            add(u1);
            add(u2);
            add(u3);
        }};

        return list;
    }

    @Cacheable
    public User getUserById(Long id) {
        return new User(id, "Ming_" + System.currentTimeMillis(), 28);
    }
}
