package org.apache.ignite.replication.publisher;

import org.apache.ignite.replication.kafka.KafkaFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.cache.integration.CacheWriterException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaIdSequencer implements IdSequencer {
    private final String topic;
    private final Producer producer;

    public KafkaIdSequencer(String topic, KafkaFactory kafkaFactory, DataRecoveryConfig dataRecoveryConfig) {
        this.topic = topic;
        this.producer = kafkaFactory.producer(dataRecoveryConfig.getProducerConfig());
    }

    @SuppressWarnings("unchecked")
    @Override public long getNextId() {
        try {
            Future<RecordMetadata> future = producer.send(new ProducerRecord(topic, 0, null, null));
            return future.get().offset();
        } catch (ExecutionException | InterruptedException e) {
            throw new CacheWriterException(e);
        }
    }
}
