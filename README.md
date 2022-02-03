# Pause-Flap

This project demonstrate SmallRye's Internal State Inconsistency when using KafkaClientService
to pause/resume channel

## Running the application in dev mode
simply run `./mvnw compile quarkus:dev`

## General flow
The `MessageConsumer` when receiving first message will go to sleep for 5 seconds and then
pause the channel `sample` while `PeriodicMessagePublisher` will periodically send message
through `sample` channel every second. 

### expected
After the channel is paused, the `consumer` will eventually stop receiving messages (after the internal smallrye buffer is drained).

### reality
The channel gets unpaused and the `consumer` keeps receiving messages.

### explanation

1. The `KafkaClientService` returns `ReactiveKafkaConsumer` which has internal state `paused` to 
indicate if a channel is paused or not. 

2. The `KafkaRecordStreamSubscription` which polls records from the given consumer client 
and emits records downstream also keeps an internal states.

3. In this demonstration, after the `MessageConsumer` paused the channel using `KafkaClientService`, the `KafkaRecordStreamSubscription` 
internal state is still `STATE_POLLING` while `ReactiveKafkaConsumer`'s internal state became `paused`.

4. after 5 secs its buffer will be full because downstream `MessageConsumer` is blocking(sleeping).

5. `KafkaRecordStreamSubscription` will call `pauseResume()` to change state to `STATE_PAUSED` because the buffer is full.

6. When the `sampleMessageConsumer` wakes up and process the message from `KafkaRecordStreamSubscription`'s internal buffer,
all the messages will be drained and its method `pauseResume()` will be called to check if the number of records in buffer is smaller
than half of its internal buffer's `maxQueueSize`(max_poll_records * max_queue_size_factor)

7. if so, it will change the state to `STATE_POLLING` and resume the channel which `consumer` previously explicitly paused

There are inconsistencies within SmallRye's packages which reproduce this conflict of channel pausing/resuming.

