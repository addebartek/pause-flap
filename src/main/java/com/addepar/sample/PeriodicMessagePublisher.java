package com.addepar.sample;

import com.addepar.sample.messaging.SampleMessage;
import com.addepar.sample.messaging.SampleMessagePublisher;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Naveen Kasthuri (naveen.kasthuri@addepar.com)
 */
@ApplicationScoped
public class PeriodicMessagePublisher {

  @Inject
  SampleMessagePublisher sampleMessagePublisher;

  @Scheduled(every="5s")
  void increment() {
    sampleMessagePublisher.publishMessage(new SampleMessage());
  }
}
