package com.example;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PeriodicMessagePublisher {
  Logger logger = LoggerFactory.getLogger(PeriodicMessagePublisher.class);

  @Inject @Channel("sample-publish-channel")
  Emitter<Message> emitter;

  @Scheduled(every="1s")
  public void publishMessage() {
    emitter.send(new Message()).whenComplete((success, error) -> {
      if (error != null) {
        logger.error("Failed to publish!", error);
      } else {
        logger.info("Message published");
      }
    });
  }
}
