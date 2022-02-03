# Pause-Flap

This project demonstrate SmallRye's Internal State Inconsistency when using KafkaClientService
to pause/resume channel

## Running the application in dev mode
1. Uncomment redpanda in application.properties if no existing kafka is setup and running

2. ./mvnw compile quarkus:dev

## General flow
The `SampleMessageConsumer` when receiving first message will go to sleep for 5 seconds and then
pause the channel `sample` while `SampleMessagePublisher` will periodically send message
through `sample` channel every second. 

### expected
After the channel is paused, the `publisher` can't no longer send new message to the `consumer`.
The `consumer` will not log further new messages received(those sent out afer 5 secs) 

### reality
The `consumer` will log further messages sent from publisher after 5 seconds

### explanation
The `KafkaClientService` returns `ReactiveKafkaConsumer` which has internal state `paused` to 
indicate if a channel is paused or not. The `KafkaRecordStreamSubscription` which polls records from the given consumer client 
and emits records downstream also keeps an internal states. In this demonstration,
after the `sampleMessageConsumer` paused the channel using `KafkaClientService`, the `KafkaRecordStreamSubscription` 
internal state is still `STATE_POLLING` while `ReactiveKafkaConsumer`'s internal state became `paused`,
and after 5 secs its buffer will be full because downstream `SampleMessageConsumer` is blocking(sleeping).
The `KafkaRecordStreamSubscription` will call `pauseResume()` to change state to `STATE_PAUSED` because the buffer is full.
When the `sampleMessageConsumer` wakes up and process the message from `KafkaRecordStreamSubscription`'s internal buffer,
all the messages will be drained and its method `pauseResume()` will be called to check if the number of records in buffer is smaller
than half of its internal buffer's `maxQueueSize`(max_poll_records * max_queue_size_factor), 
if so, it will change the state to `STATE_POLLING` and resume the channel. There are inconsistencies within
SmallRye's packages which reproduce this conflict of channel pausing/resuming.

