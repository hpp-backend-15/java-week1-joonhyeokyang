package io.hhplus.tdd;

import io.hhplus.tdd.point.application.ChargePointService;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.application.PointService;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infra.SynchronizedChargePointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PointServiceTest {
    private MemoryUserPointTable memoryUserPointTable;
    private MemoryPointHistoryTable memoryPointHistoryTable;
    private PointService pointService;

    // 매 테스트마다 UserPointTable객체를 새로 생성함으로써, 항상 비어있는 PointTable을 반환하도록 한다
    @BeforeEach
    void setUp() {
        // 실제 UserPointTable 연동을 대역을 사용하여 테스트를 용이하도록 한다.
        memoryUserPointTable = new MemoryUserPointTable();
        memoryPointHistoryTable = new MemoryPointHistoryTable();
        pointService = new PointService(memoryUserPointTable, memoryPointHistoryTable);
    }

    @Test
    void shouldReturnPoint() {
        memoryUserPointTable.insertOrUpdate(1L, 0);

        UserPoint userPoint = pointService.findByUserId(1L);

        assertThat(userPoint.id()).isEqualTo(1L);
    }

    @Test
    void shouldUsePoint() {
        //given
        memoryUserPointTable.insertOrUpdate(1L, 2000L);

        //when
        UserPoint usedUserPoint = pointService.useUserPoint(1L, 1000L);

        //then
        assertThat(usedUserPoint.point()).isEqualTo(1000L);
    }

    @Test
    void 현재포인트보다_큰값을사용하는경우_예외() {
        memoryUserPointTable.insertOrUpdate(1L, 0);

        assertThatThrownBy(() -> pointService.useUserPoint(1L, 1000L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 사용하려는포인트가_0이하인경우_예외() {
        //then
        assertThatThrownBy(() -> pointService.useUserPoint(1L, -1L))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 포인트를_사용한다면_내역이저장된다() {
        //given
        memoryUserPointTable.insertOrUpdate(1L, 1000L);

        //when
        pointService.useUserPoint(1L, 1000L);
        List<PointHistory> pointHistories = memoryPointHistoryTable.selectAllByUserId(1L);

        //then
        assertThat(pointHistories).hasSize(1);
        assertThat(pointHistories.get(0).userId()).isEqualTo(1L);
        assertThat(pointHistories.get(0).amount()).isEqualTo(0L);
        assertThat(pointHistories.get(0).type()).isEqualTo(USE);

    }

    @Test
    void 포인트_사용내역을_조회할수있다() {
        //given
        memoryUserPointTable.insertOrUpdate(1L, 1000L);

        //when
        pointService.useUserPoint(1L, 1000L);
        List<PointHistory> pointHistories = pointService.findAllPointHistoryByUserId(1L);

        assertThat(pointHistories.get(0).userId()).isEqualTo(1L);
        assertThat(pointHistories.get(0).amount()).isEqualTo(0L);
        assertThat(pointHistories.get(0).type()).isEqualTo(USE);
    }


    //    @Test
//    void 만약충전하려는포인트id가_존재하지않는다면_예외발생() {
//
//        assertThatThrownBy(() -> pointService.chargeUserPoint(1L, 1000L))
//                .isEqualTo(NoSuchElementException.class);
//    }

}
