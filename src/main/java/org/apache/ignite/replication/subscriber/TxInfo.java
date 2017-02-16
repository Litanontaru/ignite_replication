package org.apache.ignite.replication.subscriber;

import org.apache.ignite.lang.IgniteBiTuple;

import java.util.List;
import java.util.UUID;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 11:10 AM
 */
public class TxInfo {
    private final UUID consumerId;
    private final long id;
    private final List<IgniteBiTuple<String, Object>> scope;

    public TxInfo(UUID consumerId, long id, List<IgniteBiTuple<String, Object>> scope) {
        this.consumerId = consumerId;
        this.id = id;
        this.scope = scope;
    }

    public UUID consumerId() {
        return consumerId;
    }

    public long getId() {
        return id;
    }

    public List<IgniteBiTuple<String, Object>> getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "TxInfo{" + consumerId + ", " + id + ", " + scope + '}';
    }
}
