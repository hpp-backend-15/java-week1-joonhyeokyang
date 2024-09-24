package io.hhplus.tdd.point;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class UserPointTest {
    private UserPoint userPoint;

    @BeforeEach
    public void setUp() {
        userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
    }

    @Test
    public void 포인트를_충전하면_충전돼야한다() {
        UserPoint chargedPoint = userPoint.chargePoint(1000L);
        assertThat(chargedPoint.point()).isEqualTo(2000L);
    }

    @Test
    public void 충전하려는포인트가_0이하인경우_예외() {
        assertThatThrownBy(() -> userPoint.chargePoint(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test

    public void 갖고있는포인트보다_사용하는포인트가큰경우_예외() {
        assertThatThrownBy(() -> userPoint.usePoint(2000L))
                .isInstanceOf(IllegalStateException.class);
    }
}
