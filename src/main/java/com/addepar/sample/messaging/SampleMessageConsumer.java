package com.addepar.sample.messaging;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Naveen Kasthuri (naveen.kasthuri@addepar.com)
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

  @Incoming("sample-consume-channel")
  @Blocking
  void consume(SampleMessage message) {
    if (count == 0) {
        // we sleep on receiving the first message, block the following messages until wake up
      try {
        logger.info("before sleep");

        kafkaClientService
                .getConsumer("sample-consume-channel")
                .paused()
                .await()
                .indefinitely()
                .forEach(topicPartition -> logger.info("paused: {}", topicPartition.topic()));

        // we sleep for 30 sec for the smallrye buffer to be filled
        Thread.sleep(30000);

        logger.info("after sleep");

        kafkaClientService
                .getConsumer("sample-consume-channel")
                .paused()
                .await()
                .indefinitely()
                .forEach(topicPartition -> logger.info("paused: {}", topicPartition.topic()));

        logger.info("after pausing");
        logger.error("pausing {}", kafkaClientService.getConsumer("sample-consume-channel").pause().await().indefinitely());
        logger.info("done pausing");

        kafkaClientService
                .getConsumer("sample-consume-channel")
                .paused()
                .await()
                .indefinitely()
                .forEach(topicPartition -> logger.info("paused: {}", topicPartition.topic()));

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
            .forEach(topicPartition -> logger.info("paused: {}", topicPartition.topic()));
  }
}
