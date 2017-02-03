package org.apache.ignite.replication.message;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransactionMessageBuilder {
    private final TransactionMetadataBuilder metadataBuilder = new TransactionMetadataBuilder();
    private final List<List> values = new ArrayList<>();

    public void setTransactionId(long transactionId) {
        metadataBuilder.setTransactionId(transactionId);
    }

    public void addCacheEntries(String cacheName, Collection<Cache.Entry<?, ?>> entries) {
        List<Object> cacheValues = new ArrayList<>(entries.size());

        metadataBuilder.addNextCache(cacheName, entries.size());
        values.add(cacheValues);
        for (Cache.Entry<?, ?> entry : entries) {
            metadataBuilder.addKey(entry.getKey());
            cacheValues.add(entry.getValue());
        }
    }

    public TransactionMessage build() {
        return new TransactionMessage(metadataBuilder.build(), values);
    }
}
