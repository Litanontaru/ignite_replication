package org.apache.ignite.replication.message;

import java.util.List;

public class TransactionMessage {

    public final TransactionMetadata metadata;
    public final List<List> values;

    public TransactionMessage(TransactionMetadata metadata, List<List> values) {
        this.metadata = metadata;
        this.values = values;
    }
}
