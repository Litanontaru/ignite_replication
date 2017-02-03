package org.apache.ignite.replication.publisher;

import java.io.Serializable;
import java.util.Properties;

public class DataRecoveryConfig implements Serializable {
    private String remoteTopic;
    private String localTopic;
    private String reconciliationTopic;
    private Properties consumerConfig;
    private Properties producerConfig;

    public String getRemoteTopic() {
        return remoteTopic;
    }

    public void setRemoteTopic(String remoteTopic) {
        this.remoteTopic = remoteTopic;
    }

    public String getLocalTopic() {
        return localTopic;
    }

    public void setLocalTopic(String localTopic) {
        this.localTopic = localTopic;
    }

    public String getReconciliationTopic() {
        return reconciliationTopic;
    }

    public void setReconciliationTopic(String reconciliationTopic) {
        this.reconciliationTopic = reconciliationTopic;
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
