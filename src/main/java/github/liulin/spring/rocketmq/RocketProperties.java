package github.liulin.spring.rocketmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liulin
 * @version $Id: RocketProperties.java, v0.1 2020/7/14 15:03 liulin Exp $$
 */
@ConfigurationProperties(prefix = "rocketmq")
public class RocketProperties {
    private String namesrvName;
    private String producerGroup;

    public String getNamesrvName() {
        return namesrvName;
    }

    public void setNamesrvName(String namesrvName) {
        this.namesrvName = namesrvName;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }
}
