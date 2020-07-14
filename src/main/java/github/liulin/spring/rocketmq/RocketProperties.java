package github.liulin.spring.rocketmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liulin
 * @version $Id: RocketProperties.java, v0.1 2020/7/14 15:03 liulin Exp $$
 */
@ConfigurationProperties(prefix = "rocketmq")
public class RocketProperties {
    private String namesrvAddr;
    private String producerGroup;

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }
}
