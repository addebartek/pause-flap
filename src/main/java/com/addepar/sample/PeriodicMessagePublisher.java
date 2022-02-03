package com.addepar.sample;

import com.addepar.sample.messaging.SampleMessage;
import com.addepar.sample.messaging.SampleMessageConsumer;
import com.addepar.sample.messaging.SampleMessagePublisher;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Michael Fang (michael.fang@addepar.com)
 */
@ApplicationScoped
public class PeriodicMessagePublisher {
  private static final Logger logger = LoggerFactory.getLogger(PeriodicMessagePublisher.class);


  @Inject
  SampleMessagePublisher sampleMessagePublisher;

  @Scheduled(every="1s")
  void increment() {

    logger.warn("sent out msg");
    sampleMessagePublisher.publishMessage(new SampleMessage());
  }
}
