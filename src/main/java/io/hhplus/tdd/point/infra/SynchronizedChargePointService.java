package io.hhplus.tdd.point.infra;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.application.ChargePointService;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;

@Service
@RequiredArgsConstructor
public class SynchronizedChargePointService implements ChargePointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public synchronized UserPoint charge(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint chargedPoint = userPoint.chargePoint(amount);
        pointHistoryTable.insert(id, chargedPoint.point(), CHARGE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }
}
