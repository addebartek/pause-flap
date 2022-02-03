package com.addepar.sample.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Michael Fang (michael.fang@addepar.com)
 */
public final class SampleMessage {

  @JsonProperty("body")
  private final String body;

  public SampleMessage() {
    this.body = "Random: " + RandomStringUtils.randomAlphabetic(5);
  }

  public SampleMessage(String message) {
    this.body = message;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("body", body)
        .toString();
  }
}
