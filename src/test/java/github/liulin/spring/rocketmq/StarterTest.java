package github.liulin.spring.rocketmq;

import github.liulin.spring.rocketmq.autoconfigure.RocketMqAutoConfigure;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class StarterTest {

    @Test
    public void autoConfigTest() throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RocketMqAutoConfigure.class,TestConfigure.class);
//        context.
        Thread.currentThread().join();
    }
}
