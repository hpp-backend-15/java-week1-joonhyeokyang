package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;

import java.util.HashMap;

public class MemoryUserPointTable extends UserPointTable {
    private HashMap<Long, UserPoint> map = new HashMap<>();
    @Override
    public UserPoint selectById(Long id) {
        return map.get(id);
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
        return map.put(id, userPoint);
    }
}
