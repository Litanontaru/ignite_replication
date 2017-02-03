package org.apache.ignite.replication.message;

import org.apache.ignite.lang.IgniteBiTuple;

import java.io.Serializable;
import java.util.List;

public class TransactionMetadata implements Serializable {
    private final long transactionId;
    private final List<IgniteBiTuple<String, List>> cachesKeys;

    public TransactionMetadata(long transactionId, List<IgniteBiTuple<String, List>> cachesKeys) {
        this.transactionId = transactionId;
        this.cachesKeys = cachesKeys;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public TransactionScopeIterator scopeIterator() {
        return new LinearTransactionScopeIterator(cachesKeys);
    }
}
