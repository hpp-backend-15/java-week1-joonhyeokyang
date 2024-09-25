package io.hhplus.tdd.point.domain;

public interface ChargePointService {
    UserPoint charge(UserPoint userPoint, long amount);
}
