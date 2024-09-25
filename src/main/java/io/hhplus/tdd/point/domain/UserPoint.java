package io.hhplus.tdd.point.domain;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public static final long MAX_USER_POINT = 1_000_000_000L;
    public static final long MAX_CHARGE_POINT = 1_000_000_000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint chargePoint(long point) {
        if (point > MAX_CHARGE_POINT) {
            throw new IllegalArgumentException("충전하려는 포인트는 MAX_CHARGE_POINT를 넘을 수 없습니다.");
        }

        if (point <= 0) {
            throw new IllegalArgumentException("충전하려는 포인트는 0 이하일 수 없습니다.");
        }

        if (this.point + point > MAX_USER_POINT) {
            throw new IllegalStateException("유저의 포인트는 MAX_USER_POINT를 넘을 수 없습니다.");
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
