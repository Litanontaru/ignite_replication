package org.apache.ignite.replication.boot;

import kafka.admin.AdminUtils;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.*;

import static java.lang.Thread.sleep;

public class EmbeddedKafka {
    private static final String LOCALHOST = "localhost";
    private static final int ZOOKEEPER_TICK_TIME = 500;
    private static final int ZOOKEEPER_SESSION_TIMEOUT = 8_000;
    private static final int ZOOKEEPER_CONNECTION_TIMEOUT = 8_000;
    private static final Set<String> INTERNAL_TOPICS = new HashSet<String>() {{
        add("__consumer_offsets");
    }};

    private final int numberOfKafkaBrokers;
    private final int zookeeperPort;
    private final int kafkaPort;

    private ServerCnxnFactory factory;
    private final List<KafkaServerStartable> brokers = new ArrayList<>();

    public EmbeddedKafka(int numberOfKafkaBrokers, int zookeeperPort, int kafkaPort) {
        this.numberOfKafkaBrokers = numberOfKafkaBrokers;
        this.zookeeperPort = zookeeperPort;
        this.kafkaPort = kafkaPort;
    }

    public void setUp() {
        try {
            startZookeeper();
            startKafkaServers();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void tearDown() {
        deleteAllTopics();
        for (KafkaServerStartable broker : brokers) {
            broker.shutdown();
            broker.awaitShutdown();
        }
        factory.shutdown();
    }

    private void startZookeeper() throws IOException, InterruptedException {
        factory = NIOServerCnxnFactory.createFactory(new InetSocketAddress(LOCALHOST, zookeeperPort), 10);
        File snapshotDir = Files.createTempDirectory("embedded-zk-snapshot-" + zookeeperPort).toFile();
        File logDir = Files.createTempDirectory("embedded-zk-logs-" + zookeeperPort).toFile();
        factory.startup(new ZooKeeperServer(snapshotDir, logDir, ZOOKEEPER_TICK_TIME));
    }

    private void startKafkaServers() throws IOException {
        for (int i = 0; i < numberOfKafkaBrokers; i++) {
            int port = kafkaPort + i;
            File logDir = Files.createTempDirectory(String.format("kafka-local-%s-%s", kafkaPort, i)).toFile();

            Properties properties = new Properties();
            properties.setProperty("zookeeper.connect", String.format("%s:%s", LOCALHOST, zookeeperPort));
            properties.setProperty("broker.id", String.valueOf(i + 1));
            properties.setProperty("host.name", LOCALHOST);
            properties.setProperty("port", Integer.toString(port));
            properties.setProperty("log.dir", logDir.getAbsolutePath());
            properties.setProperty("log.flush.interval.messages", String.valueOf(1));
            properties.setProperty("log.retention.ms", String.valueOf(Long.MAX_VALUE));
            properties.setProperty("controlled.shutdown.enable", String.valueOf(false));
            properties.setProperty("delete.topic.enable", String.valueOf(true));

            KafkaServerStartable broker = startBroker(properties);
            brokers.add(broker);
        }
    }

    private KafkaServerStartable startBroker(Properties props) {
        KafkaServerStartable server = new KafkaServerStartable(new KafkaConfig(props));
        server.startup();
        return server;
    }

    private void deleteAllTopics() {
        ZkClient zkClient = null;
        ZkUtils zkUtils = null;

        try {
            String zookeeperConnect = String.format("%s:%s", LOCALHOST, zookeeperPort);

            zkClient = new ZkClient(
                    zookeeperConnect,
                    ZOOKEEPER_SESSION_TIMEOUT,
                    ZOOKEEPER_CONNECTION_TIMEOUT,
                    ZKStringSerializer$.MODULE$);
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), false);
            for (String topic : listTopics()) {
                if (!INTERNAL_TOPICS.contains(topic)) {
                    AdminUtils.deleteTopic(zkUtils, topic);
                }
            }
        }
        finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }

    private Collection<String> listTopics() {
        Properties consumerConfig = new Properties() {{
            setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", LOCALHOST, kafkaPort));
            setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
            setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        }};
        try (Consumer<Long, Long> consumer = new KafkaConsumer<>(consumerConfig)) {
            return consumer.listTopics().keySet();
        }
    }
}
