package org.apache.ignite.replication.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KafkaFactoryImpl implements KafkaFactory {
    private final Queue<Closeable> closeables = new ConcurrentLinkedQueue<>();

    public void closeAll() {
        while (!closeables.isEmpty())
            try {
                closeables.poll().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public <K, V> Producer<K, V> producer(Properties properties) {
        return add(new KafkaProducer<>(properties));
    }

    @Override
    public <K, V> Consumer<K, V> consumer(Properties properties) {
        return add(new KafkaConsumer<>(properties));
    }

    private <T extends Closeable> T add(T t) {
        closeables.add(t);
        return t;
    }
}
