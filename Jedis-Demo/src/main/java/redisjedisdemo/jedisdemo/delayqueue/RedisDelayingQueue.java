package redisjedisdemo.jedisdemo.delayqueue;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

public class RedisDelayingQueue<T> {

    static class TaskItem<T> {
        public String id;
        public T msg;
    }

    private Type TaskType = new TypeReference<TaskItem<T>>() {
    }.getType();

    private Jedis jedis;
    private String queueKey;

    public RedisDelayingQueue(Jedis jedis, String queueKey) {
        this.jedis = jedis;
        this.queueKey = queueKey;
    }

    public void delay(T msg) {
        TaskItem<T> task = new TaskItem<T>();
        task.id = UUID.randomUUID().toString(); // 分配唯一的 uuid
        task.msg = msg;
        String s = JSON.toJSONString(task); // fastjson 序列化
        jedis.zadd(queueKey, System.currentTimeMillis() + 5000, s); // 塞入延时队列 ,5s 后再试
    }

    public void loop() {
        while (!Thread.interrupted()) {
            // 只取一条
            // 这里取到0~当前时间，相当于从延迟队列中只取5s前的数据
            Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if (values.isEmpty()) {
                try {
                    Thread.sleep(500); // 歇会继续
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            String s = values.iterator().next();
            if (jedis.zrem(queueKey, s) > 0) { // 抢到了
                TaskItem<T> task = JSON.parseObject(s, TaskType); // fastjson 反序列化
                this.handleMsg(task.msg);
            }
        }
    }

    public void handleMsg(T msg) {
        System.out.println(msg + ": " + Thread.currentThread());
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        RedisDelayingQueue<String> queue = new RedisDelayingQueue<>(jedis, "q-demo");

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                queue.delay("codehole" + i);
            }
        });

        Thread consumer1 = new Thread(() -> queue.loop());

        producer.start();
        consumer1.start();
        try {
            // join() producer线程开始执行，当前线程等待producer执行后才能继续执行
            producer.join();
            Thread.sleep(6000);
            consumer1.interrupt();
            consumer1.join();
        } catch (InterruptedException e) {
        }
    }
}
