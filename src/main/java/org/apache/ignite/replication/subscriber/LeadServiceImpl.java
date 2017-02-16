package org.apache.ignite.replication.subscriber;

import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.services.ServiceContext;

import java.util.List;
import java.util.UUID;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 12:10 PM
 */
public class LeadServiceImpl implements LeadService {
    private transient Lead lead;

    @Override
    public List<Long> notifyRead(UUID consumer, long id, List<IgniteBiTuple<String, Object>> scope) {
        return lead.notifyRead(consumer, id, scope);
    }

    @Override
    public void notifyCommitted(UUID consumer, List<Long> ids) {
        lead.notifyCommitted(consumer, ids);
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
