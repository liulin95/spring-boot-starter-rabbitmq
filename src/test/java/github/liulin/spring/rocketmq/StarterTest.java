package github.liulin.spring.rocketmq;

import github.liulin.spring.rocketmq.configure.RocketMqConfigure;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.concurrent.*;

public class StarterTest {

    @Test
    public void autoConfigTest() throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfigure.class);
//        context.
        Thread.currentThread().join();
    }

    @Test
    public void mapTest(){
        Map<String,Integer> map = new ConcurrentHashMap();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            threadPoolExecutor.execute(()->{
                try {
                    countDownLatch.await();
                    if(!map.containsKey("123")){
//                        Thread.sleep(500l);
                        map.put("123",temp);
                        System.out.println("thread "+temp+" put success");
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
