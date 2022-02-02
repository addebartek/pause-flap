package com.addepar.sample.messaging;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SampleMessagePublisher {
  Logger logger = LoggerFactory.getLogger(SampleMessagePublisher.class);

  @Inject @Channel("sample-publish-channel")
  Emitter<SampleMessage> emitter;

  public void publishMessage(SampleMessage message) {
    emitter.send(message).whenComplete((success, error) -> {
      if (error != null) {
        logger.error("Failed to publish {}!", message, error);
      }
    });
  }
}
