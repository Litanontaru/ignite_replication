package org.apache.ignite.replication.subscriber;

import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.services.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 12:09 PM
 */
public interface LeadService extends Service {
    List<Long> notifyRead(UUID consumer, long id, List<IgniteBiTuple<String, Object>> scope);

    void notifyCommitted(UUID consumer, List<Long> ids);
}
