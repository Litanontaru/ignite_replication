package org.apache.ignite.replication.publisher;

import org.apache.ignite.replication.kafka.KafkaFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import javax.cache.Cache;
import javax.cache.integration.CacheWriterException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

public class TransactionalKafkaProducer {
    private final String dataTopic;
    private final int partitions;
    private final Producer producer;
    private final Serializer serializer;

    public TransactionalKafkaProducer(DataRecoveryConfig dataRecoveryConfig, Serializer serializer, KafkaFactory kafkaFactory) {
        this.dataTopic = dataRecoveryConfig.getRemoteTopic();
        this.producer = kafkaFactory.producer(dataRecoveryConfig.getProducerConfig());
        this.serializer = serializer;
        partitions = producer.partitionsFor(dataTopic).size();
    }

    @SuppressWarnings("unchecked")
    public Future<RecordMetadata> writeTransaction(long transactionId, Map<String, Collection<Cache.Entry<?, ?>>> updates) throws CacheWriterException {
        try {
            int partition = Long.hashCode(transactionId) % partitions;
            Object key = serializer.serialize(transactionId);
            Object value = serializer.serialize(updates);
            ProducerRecord record = new ProducerRecord(dataTopic, partition, key, value);
            return producer.send(record);
        } catch (Exception e) {
            throw new CacheWriterException(e);
        }
    }
}
