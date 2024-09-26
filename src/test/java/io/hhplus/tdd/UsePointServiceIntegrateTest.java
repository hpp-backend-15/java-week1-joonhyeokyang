package io.hhplus.tdd;

import io.hhplus.tdd.point.application.ChargePointService;
import io.hhplus.tdd.point.application.PointService;
import io.hhplus.tdd.point.application.UsePointService;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infra.service.point.SynchronizedUsePointService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.hhplus.tdd.point.domain.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;


@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class UsePointServiceIntegrateTest {
    @Autowired
    ChargePointService chargePointService;

    @Autowired
    UsePointService usePointService;

    @Autowired
    PointService pointService;

    int numberOfThreads = 100;
    List<Integer> executionOrder;
    ExecutorService executorService;
    CountDownLatch latch; // 스레드 종료를 대기하기 위한 Latch

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(16);
        executionOrder = new ArrayList<>();
        latch = new CountDownLatch(numberOfThreads);
    }


    @Test
    void shouldUsePoint() {
        //given
        chargePointService.charge(1L, 2000L);

        //when
        UserPoint usedUserPoint = usePointService.use(1L, 1000L);

        //then
        Assertions.assertThat(usedUserPoint.point()).isEqualTo(1000L);
    }

    @Test
    void 현재포인트보다_큰값을사용하는경우_예외() {
        //given
        assertThatThrownBy(() -> usePointService.use(1L, 1000L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 사용하려는포인트가_0이하인경우_예외() {
        //given

        //then
        assertThatThrownBy(() -> usePointService.use(1L, -1L))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void 포인트를_사용한다면_내역이저장된다() {
        //given
        chargePointService.charge(1L, 1000L);
        usePointService.use(1L, 1000L);

        //when
        List<PointHistory> pointHistories = pointService.findAllPointHistoryByUserId(1L);

        //then
        Assertions.assertThat(pointHistories).hasSize(2);
        Assertions.assertThat(pointHistories.get(1).userId()).isEqualTo(1L);
        Assertions.assertThat(pointHistories.get(1).amount()).isEqualTo(0L);
        Assertions.assertThat(pointHistories.get(1).type()).isEqualTo(USE);

    }

    @Test
    void 포인트_사용내역을_조회할수있다() {
        //given
        chargePointService.charge(1L, 1000L);

        //when
        usePointService.use(1L, 1000L);

        //then
        List<PointHistory> pointHistories = pointService.findAllPointHistoryByUserId(1L);
        Assertions.assertThat(pointHistories.get(1).userId()).isEqualTo(1L);
        Assertions.assertThat(pointHistories.get(1).amount()).isEqualTo(0L);
        Assertions.assertThat(pointHistories.get(1).type()).isEqualTo(USE);
    }


    @Test
    void synchronized_포인트충전은_동시성문제를_해결한다() throws Exception {
        //given
        chargePointService.charge(1L, 100L);

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
