package org.apache.ignite.replication.publisher;

import java.io.Serializable;
import java.util.Properties;

public class DataRecoveryConfig implements Serializable {
    private String remoteTopic;
    private Properties consumerConfig;
    private Properties producerConfig;

    public String getRemoteTopic() {
        return remoteTopic;
    }

    public void setRemoteTopic(String remoteTopic) {
        this.remoteTopic = remoteTopic;
    }

    public Properties getConsumerConfig() {
        return consumerConfig;
    }

    public void setConsumerConfig(Properties consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public Properties getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(Properties producerConfig) {
        this.producerConfig = producerConfig;
    }
}
