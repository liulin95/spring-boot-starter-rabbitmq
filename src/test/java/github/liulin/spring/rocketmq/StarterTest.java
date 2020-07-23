package github.liulin.spring.rocketmq;

import github.liulin.spring.rocketmq.core.PullConsumer;
import github.liulin.spring.rocketmq.listener.TestPullListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StarterTest {

    @Test
    public void autoConfigTest() throws InterruptedException, UnsupportedEncodingException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfigure.class);
//        context.
        TestPullListener consumer = (TestPullListener) context.getBean("testPullListener");
        consumer.handle();
//        System.out.println(consumer.toString());
//        List<MessageExt> list = consumer.poll();
//        for (MessageExt msg : list) {
//            String content = new String(msg.getBody(), "utf-8");
//            System.out.println(content);
//        }
        Thread.currentThread().join();
    }

    @Test
    public void mapTest() {
        Map<String, Integer> map = new ConcurrentHashMap();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            threadPoolExecutor.execute(() -> {
                try {
                    countDownLatch.await();
                    if (!map.containsKey("123")) {
//                        Thread.sleep(500l);
                        map.put("123", temp);
                        System.out.println("thread " + temp + " put success");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }
        countDownLatch.countDown();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
