package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

@Service
public class PointService {
    private final UserPointTable userPointTable;

    public PointService(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    public UserPoint findByUserId(long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint chargeUserPoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint chargedPoint = userPoint.chargePoint(amount);

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }

    public UserPoint useUserPoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint usedPoint = userPoint.usePoint(amount);
        return userPointTable.insertOrUpdate(id, usedPoint.point());
    }
}
