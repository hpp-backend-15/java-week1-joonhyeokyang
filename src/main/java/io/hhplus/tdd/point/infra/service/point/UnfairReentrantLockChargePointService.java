package io.hhplus.tdd.point.infra.service.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.application.ChargePointService;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;

@RequiredArgsConstructor
public class UnfairReentrantLockChargePointService implements ChargePointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final Lock lock = new ReentrantLock();

    @Override
    public UserPoint charge(long id, long amount) {
        lock.lock();
        try {
            UserPoint userPoint = userPointTable.selectById(id);
            UserPoint chargedPoint = userPoint.chargePoint(amount);
            pointHistoryTable.insert(id, chargedPoint.point(), CHARGE, System.currentTimeMillis());
            return userPointTable.insertOrUpdate(id, chargedPoint.point());
        } finally {
            lock.unlock();
        }
    }
}