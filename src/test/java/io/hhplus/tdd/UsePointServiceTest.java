package io.hhplus.tdd;

import io.hhplus.tdd.point.application.PointService;
import io.hhplus.tdd.point.application.UsePointService;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infra.service.point.DefaultChargePointService;
import io.hhplus.tdd.point.infra.service.point.FairReentrantLockChargePointService;
import io.hhplus.tdd.point.infra.service.point.SynchronizedChargePointService;
import io.hhplus.tdd.point.infra.service.point.SynchronizedUsePointService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class UsePointServiceTest {
    UsePointService usePointService;
    PointService pointService;
    int numberOfThreads = 300;
    List<Integer> executionOrder;
    ExecutorService executorService;
    CountDownLatch latch; // 스레드 종료를 대기하기 위한 Latch
    MemoryUserPointTable memoryUserPointTable = new MemoryUserPointTable();
    MemoryPointHistoryTable memoryPointHistoryTable = new MemoryPointHistoryTable();

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(16);
        executionOrder = new ArrayList<>();
        latch = new CountDownLatch(numberOfThreads);
        pointService = new PointService(memoryUserPointTable, memoryPointHistoryTable);
    }


    @Test
    void shouldUsePoint() {
        //given
        usePointService = new SynchronizedUsePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 2000L);

        //when
        UserPoint usedUserPoint = usePointService.use(1L, 1000L);

        //then
        Assertions.assertThat(usedUserPoint.point()).isEqualTo(1000L);
    }

    @Test
    void 현재포인트보다_큰값을사용하는경우_예외() {
        //given
        usePointService = new SynchronizedUsePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 0);

        assertThatThrownBy(() -> usePointService.use(1L, 1000L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 사용하려는포인트가_0이하인경우_예외() {
        //given
        usePointService = new SynchronizedUsePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 0);

        //then
        assertThatThrownBy(() -> usePointService.use(1L, -1L))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 포인트를_사용한다면_내역이저장된다() {
        //given
        usePointService = new SynchronizedUsePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 1000L);

        //when
        usePointService.use(1L, 1000L);
        List<PointHistory> pointHistories = memoryPointHistoryTable.selectAllByUserId(1L);

        //then
        Assertions.assertThat(pointHistories).hasSize(1);
        Assertions.assertThat(pointHistories.get(0).userId()).isEqualTo(1L);
        Assertions.assertThat(pointHistories.get(0).amount()).isEqualTo(0L);
        Assertions.assertThat(pointHistories.get(0).type()).isEqualTo(USE);

    }

    @Test
    void 포인트_사용내역을_조회할수있다() {
        //given
        usePointService = new SynchronizedUsePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 1000L);

        //when
        usePointService.use(1L, 1000L);
        List<PointHistory> pointHistories = pointService.findAllPointHistoryByUserId(1L);

        //then
        Assertions.assertThat(pointHistories.get(0).userId()).isEqualTo(1L);
        Assertions.assertThat(pointHistories.get(0).amount()).isEqualTo(0L);
        Assertions.assertThat(pointHistories.get(0).type()).isEqualTo(USE);
    }


    @Test
    void synchronized_포인트충전은_동시성문제를_해결한다() throws Exception {
        //given
        usePointService = new SynchronizedUsePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 300L);

        //when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                usePointService.use(1L, 1L);
                latch.countDown();  // 작업이 끝난 후 카운트 감소
            });
        }

        latch.await();  // 모든 스레드가 끝날 때까지 대기

        //then
        assertThat(pointService.findByUserId(1L).point()).isEqualTo(0L);
    }
}
