package org.apache.ignite.replication.publisher;

import org.springframework.util.SerializationUtils;

import java.nio.ByteBuffer;

public class SerializerImpl implements Serializer {
    @Override
    public <T> ByteBuffer serialize(T obj) {
        return obj == null ? null : ByteBuffer.wrap(SerializationUtils.serialize(obj));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(ByteBuffer buffer) {
        return buffer == null ? null : (T)SerializationUtils.deserialize(buffer.array());
    }
}
