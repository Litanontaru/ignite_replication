package org.apache.ignite.replication.subscriber;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.replication.kafka.KafkaFactory;
import org.apache.ignite.replication.publisher.DataRecoveryConfig;
import org.apache.ignite.replication.publisher.Serializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import javax.cache.Cache;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 12:12 PM
 */
public class Reader {
    private static final int POLL_TIMEOUT = 200;

    private final Ignite ignite;
    private final Serializer serializer;
    private final KafkaFactory kafkaFactory;
    private final DataRecoveryConfig dataRecoveryConfig;

    private final Map<Long, Map<String, Collection<Cache.Entry<?, ?>>>> buffer = new HashMap<>();

    private volatile boolean running = false;

    public Reader(Ignite ignite, Serializer serializer, KafkaFactory kafkaFactory, DataRecoveryConfig dataRecoveryConfig) {
        this.ignite = ignite;
        this.serializer = serializer;
        this.kafkaFactory = kafkaFactory;
        this.dataRecoveryConfig = dataRecoveryConfig;
    }

    public void start() {
        running = true;
        LeadService lead = ignite.services().serviceProxy(LeadService.NAME, LeadService.class, false);
        UUID nodeId = ignite.cluster().localNode().id();

        try (Consumer<ByteBuffer, ByteBuffer> consumer = kafkaFactory.consumer(dataRecoveryConfig.getConsumerConfig())) {
            consumer.subscribe(Collections.singletonList(dataRecoveryConfig.getRemoteTopic()));

            while (running) {
                ConsumerRecords<ByteBuffer, ByteBuffer> records = consumer.poll(POLL_TIMEOUT);
                Map<Long, List<IgniteBiTuple<String, Object>>> transactions = new HashMap<>();
                for (ConsumerRecord<ByteBuffer, ByteBuffer> record : records) {
                    Long id = serializer.deserialize(record.key());
                    Map<String, Collection<Cache.Entry<?, ?>>> tx = serializer.deserialize(record.value());
                    buffer.put(id, tx);

                    List<IgniteBiTuple<String, Object>> scope = tx.entrySet()
                            .stream()
                            .flatMap(e -> e.getValue()
                                    .stream()
                                    .map(entry -> new IgniteBiTuple<String, Object>(e.getKey(), entry.getKey())))
                            .collect(Collectors.toList());
                    transactions.put(id, scope);
                }

                List<Long> toCommit = lead.notifyRead(nodeId, transactions);
                if (!toCommit.isEmpty()) {
                    for (Long id : toCommit) {
                        Map<String, Collection<Cache.Entry<?, ?>>> map = buffer.get(id);
                        for (Map.Entry<String, Collection<Cache.Entry<?, ?>>> entry : map.entrySet()) {
                            IgniteCache<Object, Object> cache = ignite.cache(entry.getKey());
                            entry.getValue().forEach(pair -> cache.put(pair.getKey(), pair.getValue()));
                        }
                    }
                    lead.notifyCommitted(toCommit);
                }
            }
        }
    }

    public void stop() {
        running = false;

    }
}
