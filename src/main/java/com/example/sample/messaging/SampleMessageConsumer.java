package com.example.sample.messaging;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Fang (michael.fang@addepar.com)
 */
@ApplicationScoped
public class SampleMessageConsumer {
  private static final Logger logger = LoggerFactory.getLogger(SampleMessageConsumer.class);


  KafkaClientService kafkaClientService;
  int count;

  @Inject
  public SampleMessageConsumer(KafkaClientService kafkaClientService) {
    this.kafkaClientService = kafkaClientService;
    int count = 0;
  }

  /**
   * Reproduce the scenario where we pause the channel which will later be unpaused automatically after smallrye's upstream buffer
   * is drained, this behavior is determined by `pause-if-no-requests`.
   *
   * expected: after first message, the channel is disabled and no further message will be processed
   *
   * reality: after buffer is drained, the channel will be enabled
   */
  @Incoming("sample-consume-channel")
  @Blocking
  void consume(SampleMessage message) {
    Set<String> pausedTopicSubscription = new HashSet<>();
    if (count == 0) {
        // we sleep on receiving the first message, block the following messages until wake up
      try {
        logger.info("now taking 5s to process the first message");
        // we sleep for 5 sec for the upstream buffer to be filled,
        // the upstream buffer is kafka.max-queue-size-factor * poll.records = 1 * 2 = 2
        Thread.sleep(5000);

        logger.info("consumer wakes up");
        logger.error("pausing {}", kafkaClientService.getConsumer("sample-consume-channel").pause().await().indefinitely());
      } catch (Exception ex) {
        // do nothing
      }
    }
    count++;
    logger.info("Received: {} {}", count, message);
    kafkaClientService
            .getConsumer("sample-consume-channel")
            .paused()
            .await()
            .indefinitely()
            .forEach(topicPartition -> pausedTopicSubscription.add(topicPartition.topic()));
    logger.info("paused topic subscription: {}", pausedTopicSubscription);
  }

}
