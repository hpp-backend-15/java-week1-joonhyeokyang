package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

@Service
public class PointService {
    private final UserPointTable userPointTable;

    public PointService(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    public UserPoint findByUserId(long id) {
        if(id <0) throw new IllegalArgumentException("invalid id");
        return userPointTable.selectById(id);
    }
}
