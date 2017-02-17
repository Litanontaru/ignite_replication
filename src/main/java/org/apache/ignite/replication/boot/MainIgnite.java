package org.apache.ignite.replication.boot;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.replication.kafka.EmbeddedKafka;
import org.apache.ignite.transactions.Transaction;

import static java.lang.Thread.sleep;
import static org.apache.ignite.transactions.TransactionConcurrency.OPTIMISTIC;
import static org.apache.ignite.transactions.TransactionIsolation.SERIALIZABLE;

public class MainIgnite {
    public static void main(String[] args) throws InterruptedException {
        EmbeddedKafka embeddedKafka = new EmbeddedKafka(3, 2182, 9096);
        embeddedKafka.setUp();
        Ignite ignite = Ignition.start("ignite-config.xml");

        //Add more nodes
        Ignition.start("ignite-config.xml");
        Ignition.start("ignite-config.xml");

        System.out.println("Started");

        IgniteCache<Object, Object> userCache = ignite.cache("userCache");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 2; j++) {
                try (Transaction tx = ignite.transactions().txStart(OPTIMISTIC, SERIALIZABLE)) {
                    userCache.put(10 * i    , i);
                    userCache.put(10 * i + 1, i);
                    tx.commit();
                }
            }
        }

        System.out.println("Done");
        Ignition.stopAll(true);

        sleep(10000);

        embeddedKafka.tearDown();
    }
}
