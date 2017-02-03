package org.apache.ignite.replication.message;

public interface TransactionScopeIterator {
    boolean hasNextEntry();

    void advance();

    Object getKey();

    boolean hasNextCache();

    String nextCache();
}
