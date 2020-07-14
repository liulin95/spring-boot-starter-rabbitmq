package github.liulin.spring.rocketmq;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "github.liulin.spring.rocketmq.listener")
public class TestConfigure {
}
