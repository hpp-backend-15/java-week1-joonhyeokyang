package io.hhplus.tdd;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PointServiceTest {
    private MemoryUserPointTable memoryUserPointTable;
    private PointService pointService;

    // 매 테스트마다 UserPointTable객체를 새로 생성함으로써, 항상 비어있는 PointTable을 반환하도록 한다
    @BeforeEach
    void setUp() {
        // 실제 UserPointTable 연동을 대역을 사용하여 테스트를 용이하도록 한다.
        memoryUserPointTable = new MemoryUserPointTable();
        pointService = new PointService(memoryUserPointTable);
    }

    @Test
    void shouldReturnPoint() {
        memoryUserPointTable.insertOrUpdate(1L, 0);

        UserPoint userPoint = pointService.findByUserId(1L);

        assertThat(userPoint.id()).isEqualTo(1L);
    }

    @Test
    void shouldChargePoint() {
        memoryUserPointTable.insertOrUpdate(1L, 0);

        UserPoint chargedPoint = pointService.chargeUserPoint(1L, 1000L);

        assertThat(chargedPoint.point()).isEqualTo(1000L);
    }

    @Test
    void shouldUsePoint() {
        memoryUserPointTable.insertOrUpdate(1L, 2000L);

        UserPoint usedUserPoint = pointService.useUserPoint(1L, 1000L);
        assertThat(usedUserPoint.point()).isEqualTo(1000L);
    }

    @Test
    void 현재포인트보다_큰값을사용하는경우_예외() {
        memoryUserPointTable.insertOrUpdate(1L, 0);

        assertThatThrownBy(() -> pointService.useUserPoint(1L, 1000L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 사용하려는포인트가_0이하인경우_예외() throws Exception {
        //then
        assertThatThrownBy(() -> pointService.useUserPoint(1L, -1L))
                .isInstanceOf(IllegalArgumentException.class);
    }


//    @Test
    void 만약충전하려는포인트id가_존재하지않는다면_예외발생() {

        assertThatThrownBy(() -> pointService.chargeUserPoint(1L, 1000L))
                .isEqualTo(NoSuchElementException.class);
    }

}
