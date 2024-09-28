package io.hhplus.tdd;

import io.hhplus.tdd.point.application.ChargePointService;
import io.hhplus.tdd.point.application.PointService;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class PointServiceIntegrateTest {
    @Autowired
    ChargePointService chargePointService;

    @Autowired
    private PointService pointService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldReturnPoint() {
        UserPoint userPoint = pointService.findByUserId(1L);

        assertThat(userPoint.id()).isEqualTo(1L);
    }

    @Test
    void shouldReturnPointHistory() {
        UserPoint charged = chargePointService.charge(1L, 1L);

        List<PointHistory> allPointHistoryByUserId = pointService.findAllPointHistoryByUserId(1L);

        assertThat(allPointHistoryByUserId).hasSize(1);
        assertThat(allPointHistoryByUserId.get(0).id()).isEqualTo(1L);
    }


    //    @Test
//    void 만약충전하려는포인트id가_존재하지않는다면_예외발생() {
//
//        assertThatThrownBy(() -> pointService.chargeUserPoint(1L, 1000L))
//                .isEqualTo(NoSuchElementException.class);
//    }

}
