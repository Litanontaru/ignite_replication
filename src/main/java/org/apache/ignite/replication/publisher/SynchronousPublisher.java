package org.apache.ignite.replication.publisher;

import javax.cache.Cache;
import javax.cache.integration.CacheWriterException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Andrei_Yakushin
 * @since 12/5/2016 1:40 PM
 */
public class SynchronousPublisher implements KeyValueListener {
    private final TransactionalKafkaProducer producer;

    public SynchronousPublisher(TransactionalKafkaProducer producer) {
        this.producer = producer;
    }

    @Override
    public void writeTransaction(long transactionId, Map<String, Collection<Cache.Entry<?, ?>>> updates)
            throws CacheWriterException {
        try {
            producer.writeTransaction(transactionId, updates).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new CacheWriterException(e);
        }
    }
}
