package org.apache.ignite.replication.subscriber;

import org.apache.ignite.Ignite;
import org.apache.ignite.replication.kafka.KafkaFactory;
import org.apache.ignite.replication.publisher.DataRecoveryConfig;
import org.apache.ignite.replication.publisher.Serializer;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.SpringResource;
import org.apache.ignite.services.ServiceContext;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 12:54 PM
 */
public class ReaderServiceImpl implements ReaderService {
    @IgniteInstanceResource
    private transient Ignite ignite;

    @SpringResource(resourceName = "serializer")
    private transient Serializer serializer;

    @SpringResource(resourceName = "kafkaFactory")
    private transient KafkaFactory kafkaFactory;

    @SpringResource(resourceName = "dataRecoveryConfig")
    private transient DataRecoveryConfig dataRecoveryConfig;

    private transient Reader reader;

    @Override
    public void cancel(ServiceContext ctx) {
        reader.stop();
    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        reader = new Reader(ignite, serializer, kafkaFactory, dataRecoveryConfig);
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        reader.start();
    }
}
