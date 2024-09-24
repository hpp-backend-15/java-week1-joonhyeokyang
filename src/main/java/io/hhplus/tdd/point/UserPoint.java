package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint chargePoint(long point) {
        if (point <= 0) {
            throw new IllegalArgumentException("충전하려는 포인트는 0 이하일 수 없습니다.");
        }
        return new UserPoint(id, this.point + point, System.currentTimeMillis());
    }

    public UserPoint usePoint(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용하려는 포인트가 0 이하일 수 없습니다");
        }
        if (this.point < amount) {
            throw new IllegalStateException("사용하려는 포인트가 현재 포인트보다 클 수 없습니다.");
        }
        return new UserPoint(id, this.point - amount, System.currentTimeMillis());
    }
}
