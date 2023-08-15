package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String URL = "/requests";
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    private ItemRequestItemsDto request;
    private ItemRequestItemsDto.ItemRequestItemsDtoBuilder builder;

    @BeforeEach
    void setupBuilder() {
        builder = ItemRequestItemsDto.builder()
                .id(1L)
                .description("Молоток на день")
                .created(LocalDateTime.now());

    }

    @Test
    void testCreateEmptyStandard() throws Exception {
        ItemRequestDto requestIn = ItemRequestDto.builder()
                .build();
        requestIn.setDescription("Молоток на день");
        ItemRequestDto requestOut = ItemRequestDto.builder()
                .id(1L)
                .description("Молоток на день")
                .created(LocalDateTime.now())
                .build();
        String json = mapper.writeValueAsString(requestIn);
        when(service.create(1L, requestIn)).thenReturn(requestOut);
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestOut.getDescription()), String.class));
    }

    @Test
    void testCreateEmptyFailNoFoundUser() throws Exception {
        ItemRequestDto requestIn = ItemRequestDto.builder()
                .build();
        requestIn.setDescription("Молоток на день");
        String error = String.format("User с id -1 не найден", -1);
        String json = mapper.writeValueAsString(requestIn);
        when(service.create(-1L, requestIn)).thenThrow(new NotFoundException(error));
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void testGetUserItemsReqEmpty() throws Exception {
        when(service.getUserItemsReq(2L)).thenReturn(new ArrayList<>());
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetUserItemsReqOneObject() throws Exception {
        request = builder.build();
        when(service.getUserItemsReq(1L))
                .thenReturn(List.of(request));
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription()), String.class));
    }

    @Test
    void testGetUserItemsReqFailNotFound() throws Exception {
        String error = String.format("Пользователь с id %d не найден", -1);
        when(service.getUserItemsReq(-1L)).thenThrow(new NotFoundException(error));
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", -1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void testGetItemsEmpty() throws Exception {
        when(service.getItems(1L, 0, 10)).thenReturn(new ArrayList<>());
        mvc.perform(get(URL + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetItemsOneObject() throws Exception {
        request = builder.build();
        when(service.getItems(1L, 0, 1)).thenReturn(List.of(request));
        mvc.perform(get(URL + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription()), String.class));
    }

    @Test
    void testGetItemsFailUserNotFound() throws Exception {
        String error = String.format("User с id %d не найден", -1);
        when(service.getItems(-1L, 0, 1)).thenThrow(new NotFoundException(error));
        mvc.perform(get(URL + "/all")
                        .header("X-Sharer-User-Id", -1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void testGetItemStandard() throws Exception {
        request = builder.build();
        when(service.getItem(1L, 1L)).thenReturn(request);
        mvc.perform(get(URL + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription()), String.class));
    }

    @Test
    void testGetItemFailUserNotFound() throws Exception {
        String error = String.format("User с id %d не найден", -1);
        when(service.getItem(-1L, 1L)).thenThrow(new NotFoundException(error));
        mvc.perform(get(URL + "/1")
                        .header("X-Sharer-User-Id", -1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void testGetItemFailRequestNotFound() throws Exception {
        String error = String.format("Request с id %d не найден", 99);
        when(service.getItem(1L, 99L)).thenThrow(new NotFoundException(error));
        mvc.perform(get(URL + "/99")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }
}
