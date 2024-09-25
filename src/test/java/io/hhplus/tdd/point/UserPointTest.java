package io.hhplus.tdd.point;

import io.hhplus.tdd.point.domain.UserPoint;
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
    void 포인트를_충전하면_충전돼야한다() {
        UserPoint chargedPoint = userPoint.chargePoint(1000L);
        assertThat(chargedPoint.point()).isEqualTo(2000L);
    }

    @Test
    void 충전하려는포인트가_0이하인경우_예외() {
        assertThatThrownBy(() -> userPoint.chargePoint(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 갖고있는포인트보다_사용하는포인트가큰경우_예외() {
        assertThatThrownBy(() -> userPoint.usePoint(2000L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 충전할시유저의포인트는_기존과합쳐1000000000을_초과할수없다() {
        assertThatThrownBy(() -> userPoint.chargePoint(999_999_000L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 한번에충전할수있는포인트는_1000000000을_초과할수없다() {
        assertThatThrownBy(() -> userPoint.chargePoint(1_000_000_001L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 한번에사용할수있는포인트는_1000000000을_초과할수없다() {
        assertThatThrownBy(() -> userPoint.usePoint(1_000_000_000L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
