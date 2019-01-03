package redisjedisdemo.jedisdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ConcurrentPoolTest {

    private static int TASK_NUM = 10;

    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    private static final CountDownLatch latch = new CountDownLatch(TASK_NUM);

    @Autowired
    private JedisPool jedisPool;


    @Test
    public void jedisGetValueTest() {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set("hello", "world");
            System.out.println("redis value is : " + jedis.get("hello"));
        }
    }


    @Test
    public void jedispoolTest () {

        // 每个线程必须单独使用一个jedis实例
        Runnable redisTask = () -> {
            try(Jedis jedis = jedisPool.getResource()) {
                jedis.set("hello", "world " + Thread.currentThread().getName());
                System.out.println("redis value is : "+ jedis.get("hello"));
                latch.countDown();
            }
        };

        for(int i = 0; i < TASK_NUM; i++){
            pool.execute(redisTask);
        }

        try {
            // 通过latch.await()等待所有的任务都执行完
            // 每个任务执行完都执行latch.countDown()将数量减1
            // 当latch的数量减至0时才执行后续代码
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All redis has been executed!");
        pool.shutdownNow();

    }
}
