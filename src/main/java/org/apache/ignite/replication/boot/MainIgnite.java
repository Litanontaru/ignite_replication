package org.apache.ignite.replication.boot;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.replication.kafka.EmbeddedKafka;

public class MainIgnite {
    public static void main(String[] args) {
        EmbeddedKafka embeddedKafka = new EmbeddedKafka(3, 2182, 9096);
        embeddedKafka.setUp();
        Ignite ignite = Ignition.start("ignite-config.xml");

        System.out.println("Started");

        IgniteCache<Object, Object> userCache = ignite.cache("userCache");
        for (int i = 0; i < 100; i++) {
            userCache.put(i, i);
        }

        Ignition.stopAll(true);
        embeddedKafka.tearDown();
    }
}
