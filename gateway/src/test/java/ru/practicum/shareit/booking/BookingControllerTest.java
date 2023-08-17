package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    BookItemRequestDto bookItemRequestDto;

    String json;

    @BeforeEach
    void setup() throws Exception {
        bookItemRequestDto = new BookItemRequestDto();
        bookItemRequestDto.setItemId(2L);
        bookItemRequestDto.setStart(LocalDateTime.now().plusHours(2));
        bookItemRequestDto.setEnd(LocalDateTime.now().plusHours(4));
        json = mapper.writeValueAsString(bookItemRequestDto);
    }

    @Test
    void testFailUnknownState() throws Exception {
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
    }

    @Test
    void testGetBookings() throws Exception {
        when(client.getBookings(eq(5L), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.of(Optional.of(bookItemRequestDto)));
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 5))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookItemRequestDto.getItemId()), Long.class));
    }

    @Test
    void testPostItem() throws Exception {
        when(client.bookItem(eq(5L), eq(bookItemRequestDto)))
                .thenReturn(ResponseEntity.of(Optional.of(bookItemRequestDto)));
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookItemRequestDto.getItemId()), Long.class));
    }

    @Test
    void testGetIdBooking() throws Exception {
        when(client.getBooking(eq(5L), eq(2L)))
                .thenReturn(ResponseEntity.of(Optional.of(bookItemRequestDto)));
        mvc.perform(get(URL + "/2")
                        .header("X-Sharer-User-Id", 5))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookItemRequestDto.getItemId()), Long.class));
    }

    @Test
    void testPatchBooking() throws Exception {
        when(client.patchBooking(eq(5L), eq(true), eq(2L)))
                .thenReturn(ResponseEntity.of(Optional.of(bookItemRequestDto)));
        mvc.perform(patch(URL + "/2?approved=true")
                        .header("X-Sharer-User-Id", 5))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookItemRequestDto.getItemId()), Long.class));
    }

    @Test
    void testFailUnknownStateGetOwners() throws Exception {
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
    }

    @Test
    void testGetBookingsOwner() throws Exception {
        when(client.getBookingsOfOwner(eq(5L), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.of(Optional.of(bookItemRequestDto)));
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", 5))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookItemRequestDto.getItemId()), Long.class));
    }
}