package io.hhplus.tdd.point.infra;

import io.hhplus.tdd.point.domain.ChargePointService;
import io.hhplus.tdd.point.domain.UserPoint;
import org.springframework.stereotype.Service;

@Service
public class SynchronizedChargePointService implements ChargePointService {
    @Override
    public synchronized UserPoint charge(UserPoint userPoint, long amount) {
        return userPoint.chargePoint(amount);
    }
}
