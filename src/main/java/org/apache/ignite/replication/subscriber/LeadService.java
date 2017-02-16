package org.apache.ignite.replication.subscriber;

import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.services.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 12:09 PM
 */
public interface LeadService extends Service {
    String NAME = "Lead";

    List<Long> notifyRead(UUID consumer, Map<Long, List<IgniteBiTuple<String, Object>>> transactions);

    void notifyCommitted(List<Long> ids);
}
