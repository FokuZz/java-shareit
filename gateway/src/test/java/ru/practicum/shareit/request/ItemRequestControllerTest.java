package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
    ItemRequestClient client;

    @Autowired
    private MockMvc mvc;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        itemRequestDto = ItemRequestDto.builder()
                .id(5L)
                .description("description")
                .build();
    }

    @Test
    void testPostItem() throws Exception {
        String json = mapper.writeValueAsString(itemRequestDto);
        when(client.post(eq(5L), eq(itemRequestDto)))
                .thenReturn(ResponseEntity.of(Optional.of(itemRequestDto)));
        this.mvc
                .perform(post(URL)
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(client.getAll(eq(5L), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.of(Optional.of(itemRequestDto)));
        this.mvc
                .perform(get(URL + "/all")
                        .header("X-Sharer-User-Id", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void testGetByUserIdRequest() throws Exception {
        when(client.getUserRequests(eq(5L)))
                .thenReturn(ResponseEntity.of(Optional.of(itemRequestDto)));
        this.mvc
                .perform(get(URL)
                        .header("X-Sharer-User-Id", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void testGetIdRequest() throws Exception {
        when(client.getId(eq(5L), eq(3L)))
                .thenReturn(ResponseEntity.of(Optional.of(itemRequestDto)));
        this.mvc
                .perform(get(URL + "/3")
                        .header("X-Sharer-User-Id", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }
}