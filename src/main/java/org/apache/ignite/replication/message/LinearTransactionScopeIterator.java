package org.apache.ignite.replication.message;

import org.apache.ignite.lang.IgniteBiTuple;

import java.util.Iterator;
import java.util.List;

public class LinearTransactionScopeIterator implements TransactionScopeIterator {
    private final Iterator<IgniteBiTuple<String, List>> cachesKeysIterator;

    private Iterator keyIterator;
    private Object currentKey;

    public LinearTransactionScopeIterator(List<IgniteBiTuple<String, List>> cachesKeys) {
        cachesKeysIterator = cachesKeys.iterator();
    }

    @Override public boolean hasNextEntry() {
        return keyIterator.hasNext();
    }

    @Override public void advance() {
        currentKey = keyIterator.next();
    }

    @Override public Object getKey() {
        return currentKey;
    }

    @Override public boolean hasNextCache() {
        return cachesKeysIterator.hasNext();
    }

    @Override public String nextCache() {
        IgniteBiTuple<String, List> cacheKeys = cachesKeysIterator.next();

        keyIterator = cacheKeys.getValue().iterator();
        return cacheKeys.getKey();
    }
}
