package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

/**
 * @author Michael Fang (michael.fang@addepar.com)
 */
public class MessageSerDes {

  public static class MessageSerializer extends ObjectMapperSerializer<Message> {

    public MessageSerializer() {
      super(new ObjectMapper());
    }
  }

  public static class MessageDeserializer extends ObjectMapperDeserializer<Message> {

    public MessageDeserializer() {
      super(Message.class);
    }
  }
}
