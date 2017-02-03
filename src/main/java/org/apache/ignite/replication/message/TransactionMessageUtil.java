package org.apache.ignite.replication.message;

public class TransactionMessageUtil {
    private TransactionMessageUtil() {
    }

    public static int partitionFor(TransactionMetadata metadata, int partitions) {
        return partitionFor(metadata.getTransactionId(), partitions);
    }

    public static int partitionFor(long transactionId, int partitions) {
        return Long.hashCode(transactionId) % partitions;
    }
}
