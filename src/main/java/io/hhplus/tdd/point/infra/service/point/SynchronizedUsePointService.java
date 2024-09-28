package io.hhplus.tdd.point.infra.service.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.application.PointService;
import io.hhplus.tdd.point.application.UsePointService;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.hhplus.tdd.point.domain.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class SynchronizedUsePointService implements UsePointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public synchronized UserPoint use(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);

        UserPoint usedPoint = userPoint.usePoint(amount);
        pointHistoryTable.insert(id, usedPoint.point(), USE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, usedPoint.point());
    }
}
