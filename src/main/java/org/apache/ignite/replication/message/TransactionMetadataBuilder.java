package org.apache.ignite.replication.message;

import org.apache.ignite.lang.IgniteBiTuple;

import java.util.ArrayList;
import java.util.List;

public class TransactionMetadataBuilder {
    private final List<IgniteBiTuple<String, List>> cachesKeys = new ArrayList<>();

    private long transactionId;

    private List<Object> currentCacheKeys;

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public void addKey(Object key) {
        currentCacheKeys.add(key);
    }

    public void addNextCache(String cacheName, int size) {
        currentCacheKeys = new ArrayList<>(size);
        cachesKeys.add(new IgniteBiTuple<String, List>(cacheName, currentCacheKeys));
    }

    public TransactionMetadata build() {
        return new TransactionMetadata(transactionId, cachesKeys);
    }
}
