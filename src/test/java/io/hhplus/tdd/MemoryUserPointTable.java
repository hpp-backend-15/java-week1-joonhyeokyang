package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;

import java.util.HashMap;

public class MemoryUserPointTable extends UserPointTable {
    private HashMap<Long, UserPoint> map = new HashMap<>();
    @Override
    public UserPoint selectById(Long id) {
        return map.getOrDefault(id, UserPoint.empty(id));
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
        map.put(id, userPoint);
        return userPoint;
    }
}
