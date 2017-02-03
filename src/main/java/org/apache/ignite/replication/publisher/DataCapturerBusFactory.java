package org.apache.ignite.replication.publisher;

import org.apache.ignite.cache.store.CacheStore;

import javax.cache.configuration.Factory;

/**
 * Created by Andrei_Yakushin on 2/3/2017.
 */
public class DataCapturerBusFactory implements Factory<CacheStore> {
    @Override
    public CacheStore create() {
        return new DataCapturerBus();
    }
}
