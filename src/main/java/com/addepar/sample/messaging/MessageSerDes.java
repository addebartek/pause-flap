package com.addepar.sample.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

/**
 * @author Naveen Kasthuri (naveen.kasthuri@addepar.com)
 */
public class MessageSerDes {

  public static class MessageSerializer extends ObjectMapperSerializer<SampleMessage> {

    public MessageSerializer() {
      super(new ObjectMapper());
    }
  }

  public static class MessageDeserializer extends ObjectMapperDeserializer<SampleMessage> {

    public MessageDeserializer() {
      super(SampleMessage.class);
    }
  }
}
