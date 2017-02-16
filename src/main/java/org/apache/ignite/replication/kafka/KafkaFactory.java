package org.apache.ignite.replication.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

public interface KafkaFactory {
    <K, V> Producer<K, V> producer(Properties properties);

    <K, V> Consumer<K, V> consumer(Properties properties);
}
