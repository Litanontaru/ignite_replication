package org.apache.ignite.replication.subscriber;

import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.replication.subscriber.util.PlannerUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 11:07 AM
 */
public class Lead {
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final Map<UUID, List<Long>> toCommit = new ConcurrentHashMap<>();

    private final List<TxInfo> all = new ArrayList<>();
    private final List<Long> inProgress = new ArrayList<>();
    private final List<Long> committed = new ArrayList<>();

    private volatile boolean running = false;

    public List<Long> notifyRead(UUID consumer, long id, List<IgniteBiTuple<String, Object>> scope) {
        tasks.add(() -> all.add(new TxInfo(consumer, id, scope)));
        List<Long> result = toCommit.remove(consumer);
        return result == null ? Collections.emptyList() : result;
    }

    public void notifyCommitted(UUID consumer, List<Long> ids) {
        tasks.add(() -> committed.addAll(ids));
    }

    public void start() {
        running = true;
        while (running) {
            while (true) {
                Runnable task = tasks.poll();
                if (task == null) {
                    break;
                } else {
                    task.run();
                }
            }
            if (!all.isEmpty()) {
                plan();
            }
        }
    }

    public void stop() {
        running = false;
    }

    private void plan() {
        all.sort(Comparator.comparingLong(TxInfo::getId));
        Map<UUID, List<Long>> ready = PlannerUtil.plan(all, committed, inProgress);
        for (Map.Entry<UUID, List<Long>> entry : ready.entrySet()) {
            inProgress.addAll(entry.getValue());
            List<Long> old = toCommit.remove(entry.getKey());
            if (old != null) {
                entry.getValue().addAll(old);
            }
            toCommit.put(entry.getKey(), entry.getValue());
        }
    }
}
