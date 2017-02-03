package org.apache.ignite.replication.publisher;

import org.apache.ignite.replication.kafka.KafkaFactory;
import org.apache.ignite.replication.message.TransactionMessage;
import org.apache.ignite.replication.message.TransactionMessageBuilder;
import org.apache.ignite.replication.message.TransactionMessageUtil;
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
        this.dataTopic = dataRecoveryConfig.getLocalTopic();
        this.producer = kafkaFactory.producer(dataRecoveryConfig.getProducerConfig());
        this.serializer = serializer;
        partitions = producer.partitionsFor(dataTopic).size();
    }

    @SuppressWarnings("unchecked")
    public Future<RecordMetadata> writeTransaction(long transactionId, Map<String,
            Collection<Cache.Entry<?, ?>>> updates) throws CacheWriterException {
        try {
            TransactionMessage message = convertToMessage(transactionId, updates);
            Object key = serializer.serialize(message.metadata);
            Object value = serializer.serialize(message.values);
            int partition = TransactionMessageUtil.partitionFor(message.metadata, partitions);
            ProducerRecord record = new ProducerRecord(dataTopic, partition, key, value);
            return producer.send(record);
        } catch (Exception e) {
            throw new CacheWriterException(e);
        }
    }

    private TransactionMessage convertToMessage(long transactionId, Map<String, Collection<Cache.Entry<?, ?>>> updates) {
        TransactionMessageBuilder messageBuilder = new TransactionMessageBuilder();
        messageBuilder.setTransactionId(transactionId);
        for (Map.Entry<String, Collection<Cache.Entry<?, ?>>> cacheToUpdates : updates.entrySet()) {
            messageBuilder.addCacheEntries(cacheToUpdates.getKey(), cacheToUpdates.getValue());
        }
        return messageBuilder.build();
    }
}
