package io.hhplus.tdd;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Test
    void shouldReturnUserPoint() throws Exception {
        //given
        String url = "/point/{id}";
        UserPoint userPoint = new UserPoint(1L, 0, System.currentTimeMillis());

        given(pointService.findByUserId(anyLong()))
                .willReturn(userPoint);

        //when
        ResultActions resultActions = mockMvc.perform(get(url, 1L));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L));
    }


    @Test
    void shouldChargeUserPoint() throws Exception {

    }
}
