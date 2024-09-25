package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint findByUserId(long id) {
        return userPointTable.selectById(id);
    }

    public UserPoint chargeUserPoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint chargedPoint = userPoint.chargePoint(amount);
        pointHistoryTable.insert(id, chargedPoint.point(), CHARGE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }

    public UserPoint useUserPoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint usedPoint = userPoint.usePoint(amount);
        pointHistoryTable.insert(id, usedPoint.point(), USE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, usedPoint.point());
    }

    public List<PointHistory> findAllPointHistoryByUserId(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }
}
