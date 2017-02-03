package org.apache.ignite.replication.publisher;

import javax.cache.Cache;
import javax.cache.integration.CacheWriterException;
import java.util.Collection;
import java.util.Map;

public interface KeyValueListener {
    void writeTransaction(long transactionId, Map<String, Collection<Cache.Entry<?, ?>>> updates) throws CacheWriterException;
}
