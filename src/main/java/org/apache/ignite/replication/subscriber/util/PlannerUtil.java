package org.apache.ignite.replication.subscriber.util;

import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.replication.subscriber.TxInfo;

import java.util.*;
import java.util.function.Function;

/**
 * @author Andrei_Yakushin
 * @since 2/16/2017 12:02 PM
 */
public class PlannerUtil {
    private static final Function<UUID, Set<IgniteBiTuple<String, Object>>> CLAIMED = key -> new HashSet<>();
    private static final Function<UUID, List<Long>> READY = key -> new ArrayList<>();

    private PlannerUtil() {

    }

    public static Map<UUID, List<Long>> plan(List<TxInfo> transactions, List<Long> committed, List<Long> inProgress) {
        long currentId = 0;

        Set<IgniteBiTuple<String, Object>> blocked = new HashSet<>();
        Map<UUID, Set<IgniteBiTuple<String, Object>>> claimed = new HashMap<>();
        Map<UUID, List<Long>> plan = new HashMap<>();
        for (TxInfo info : transactions) {
            long id = info.getId();
            if (id > currentId) {
                break;
            }
            if (!committed.contains(id)) {
                List<IgniteBiTuple<String, Object>> scope = info.getScope();
                if (inProgress.contains(id) || scope.stream().allMatch(blocked::contains)) {
                    blocked.addAll(scope);
                } else {
                    UUID consumerId = info.consumerId();
                    if (isIntersectedWithClaimed(consumerId, scope, claimed)) {
                        blocked.addAll(scope);
                    } else {
                        claimed.computeIfAbsent(consumerId, CLAIMED).addAll(scope);
                        plan.computeIfAbsent(consumerId, READY).add(id);
                    }
                }
            }
            currentId++;
        }
        return plan;
    }

    private static boolean isIntersectedWithClaimed(UUID consumerId, List<IgniteBiTuple<String, Object>> scope, Map<UUID, Set<IgniteBiTuple<String, Object>>> claimed) {
        return claimed.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(consumerId))
                .allMatch(entry -> scope.stream().anyMatch(e -> entry.getValue().contains(e)));
    }
}
