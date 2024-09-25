package io.hhplus.tdd;

import io.hhplus.tdd.point.application.ChargePointService;
import io.hhplus.tdd.point.application.PointService;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infra.DefaultChargePointService;
import io.hhplus.tdd.point.infra.FairReentrantLockChargePointService;
import io.hhplus.tdd.point.infra.SynchronizedChargePointService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import static io.hhplus.tdd.point.domain.TransactionType.CHARGE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ChargePointServiceTest {
    ChargePointService chargePointService;
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
    void shouldChargePoint() {
        //given
        chargePointService = new DefaultChargePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 0);

        //when
        UserPoint chargedPoint = chargePointService.charge(1L, 1000L);

        //then
        Assertions.assertThat(chargedPoint.point()).isEqualTo(1000L);
    }


    @Test
    void 포인트를_충전한다면_내역이저장된다() {
        //given
        chargePointService = new DefaultChargePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 0);

        //when
        chargePointService.charge(1L, 1000L);
        List<PointHistory> pointHistories = memoryPointHistoryTable.selectAllByUserId(1L);

        //then
        Assertions.assertThat(pointHistories).hasSize(1);
        Assertions.assertThat(pointHistories.get(0).userId()).isEqualTo(1L);
        Assertions.assertThat(pointHistories.get(0).amount()).isEqualTo(1000L);
        Assertions.assertThat(pointHistories.get(0).type()).isEqualTo(CHARGE);

    }


    @Test
    void 포인트_충전내역을_조회할수있다() {
        //given
        chargePointService = new DefaultChargePointService(memoryUserPointTable, memoryPointHistoryTable);
        memoryUserPointTable.insertOrUpdate(1L, 1000L);

        //when
        chargePointService.charge(1L, 1000L);
        List<PointHistory> pointHistories = pointService.findAllPointHistoryByUserId(1L);

        Assertions.assertThat(pointHistories).isNotEmpty();
        Assertions.assertThat(pointHistories.get(0).type()).isEqualTo(CHARGE);
    }

}
