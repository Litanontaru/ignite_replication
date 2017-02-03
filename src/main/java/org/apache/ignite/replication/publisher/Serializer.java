package org.apache.ignite.replication.publisher;

import java.nio.ByteBuffer;

public interface Serializer {
    <T> ByteBuffer serialize(T obj);

    <T> T deserialize(ByteBuffer buffer);
}
