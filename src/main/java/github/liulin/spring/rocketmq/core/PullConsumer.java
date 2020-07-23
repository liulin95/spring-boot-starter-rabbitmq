package github.liulin.spring.rocketmq.core;

import github.liulin.spring.rocketmq.annotation.RocketMqHandler;
import org.apache.rocketmq.client.consumer.LitePullConsumer;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public interface PullConsumer {
    @RocketMqHandler
    default List<MessageExt> poll() {
        return null;
    }

    default LitePullConsumer getConsumer() {
        return null;
    }
}
