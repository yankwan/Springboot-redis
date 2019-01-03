package redisjedisdemo.jedisdemo;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrentTest {
    private static final ExecutorService pool = Executors.newFixedThreadPool(20);

    private static final Jedis jedis = new Jedis();

    static class RedisTest implements Runnable {
        @Override
        public void run() {
            while(true) {
                jedis.set("hello", "world");
            }
        }
    }

    public static void main(String[] args) {
        for(int i = 0; i < 5; i++){
            pool.execute(new RedisTest());
        }
    }
}
