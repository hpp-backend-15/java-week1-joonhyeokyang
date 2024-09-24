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

}
