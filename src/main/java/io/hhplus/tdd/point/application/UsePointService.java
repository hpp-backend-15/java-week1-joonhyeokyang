package io.hhplus.tdd.point.application;

import io.hhplus.tdd.point.domain.UserPoint;
import org.springframework.stereotype.Service;

@Service
public interface UsePointService {
    UserPoint use(long id, long amount);
}
