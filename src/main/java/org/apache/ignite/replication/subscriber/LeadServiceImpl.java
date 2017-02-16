package org.apache.ignite.replication.subscriber;

import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.services.ServiceContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 12:10 PM
 */
public class LeadServiceImpl implements LeadService {
    private transient Lead lead;

    @Override
    public List<Long> notifyRead(UUID consumer, Map<Long, List<IgniteBiTuple<String, Object>>> transactions) {
        return lead.notifyRead(consumer, transactions);
    }

    @Override
    public void notifyCommitted(UUID consumer, List<Long> ids) {
        lead.notifyCommitted(ids);
    }

    @Override
    public void cancel(ServiceContext ctx) {
        lead.stop();
    }

    @Override
    public void init(ServiceContext ctx) throws Exception {
        lead = new Lead();
    }

    @Override
    public void execute(ServiceContext ctx) throws Exception {
        lead.start();
    }
}
