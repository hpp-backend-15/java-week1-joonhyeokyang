package io.hhplus.tdd.point.application;

import io.hhplus.tdd.point.domain.UserPoint;

public interface ChargePointService {
    UserPoint charge(long id, long amount);
}
