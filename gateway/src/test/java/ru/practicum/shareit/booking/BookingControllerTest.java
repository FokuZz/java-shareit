package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String URL = "/bookings";
    private static final String ERROR_TEXT = "Нельзя вводить отрицательные число";
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient client;

    @Autowired
    private MockMvc mvc;

    @Test
    void getAllTestValidation() throws Exception {

        //fail State
        String state = "qwerty";
        String error = "Unknown state: " + state;
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        error = ERROR_TEXT;
        //fail from -1
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "all")
                        .param("from", "-1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
        //fail size 0
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
        //fail
        error = "X-Sharer-User-Id";
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void bookingValidation() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookItemRequestDto bookingInDto = new BookItemRequestDto();
        bookingInDto.setItemId(1L);
        bookingInDto.setStart(now.plusMinutes(1));
        bookingInDto.setEnd(now.plusMinutes(2));

        //fail by start time
        bookingInDto.setItemId(1L);
        bookingInDto.setStart(null);
        String json = mapper.writeValueAsString(bookingInDto);
        String error = "start can't be NULL";
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        //fail by end time
        bookingInDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingInDto.setEnd(null);
        json = mapper.writeValueAsString(bookingInDto);
        error = "end can't be NULL";
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void getBookingsOfOwnerTestValidation() throws Exception {
        //Fail By State
        String state = "qwerty";
        String error = "Unknown state: " + state;
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        //Fail By From
        error = ERROR_TEXT;
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "all")
                        .param("from", "-1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        //Fail By Size
        error = ERROR_TEXT;
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "all")
                        .param("from", "0")
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }
}