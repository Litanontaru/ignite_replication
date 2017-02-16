package org.apache.ignite.replication.publisher;

import org.apache.ignite.cache.store.CacheStore;

import javax.cache.configuration.Factory;

public class DataCapturerBusFactory implements Factory<CacheStore> {
    @Override
    public CacheStore create() {
        return new DataCapturerBus();
    }
}
