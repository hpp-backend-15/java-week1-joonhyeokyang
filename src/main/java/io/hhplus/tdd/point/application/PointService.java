package io.hhplus.tdd.point.application;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.ChargePointService;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;

@Service
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final ChargePointService chargePointService;

    public UserPoint findByUserId(long id) {
        return userPointTable.selectById(id);
    }

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable, ChargePointService chargePointService) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
        this.chargePointService = chargePointService;
    }

    public UserPoint chargeUserPoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

//        UserPoint chargedPoint = userPoint.chargePoint(amount);
        UserPoint chargedPoint = chargePointService.charge(userPoint, amount);
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
