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

  @Inject
  public SampleMessageConsumer(KafkaClientService kafkaClientService) {
    this.kafkaClientService = kafkaClientService;
  }

  @Incoming("sample-consume-channel")
  @Blocking
  void consume(SampleMessage message) {
    logger.info("Received: {}", message);
    logger.info("pausing {}", kafkaClientService.getConsumer("sample-consume-channel").pause().await().indefinitely());
    logger.info("done pausing");
  }
}
