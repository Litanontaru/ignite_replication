package org.apache.ignite.replication.boot;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;

public class MainIgnite {
    public static void main(String[] args) {
        EmbeddedKafka embeddedKafka = new EmbeddedKafka(3, 2182, 9096);
        embeddedKafka.setUp();

        System.out.println("Started");

        Ignite ignite = Ignition.start("ignite-config.xml");

        IgniteCache<Object, Object> userCache = ignite.cache("userCache");
        for (int i = 0; i < 100; i++) {
            userCache.put(i, i);
        }

        Ignition.stopAll(true);
        embeddedKafka.tearDown();
    }
}
