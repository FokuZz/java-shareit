package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String URL = "/items";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient client;

    @Autowired
    private MockMvc mvc;

    @Test
    void testGetItems() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(client.getAll(eq(5L), anyInt(), anyInt())).thenReturn(ResponseEntity.of(Optional.of(itemDto)));
        this.mvc
                .perform(get(URL)
                        .header("X-Sharer-User-Id", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable().toString()), String.class));
    }

    @Test
    void testPostItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(itemDto);
        when(client.postItem(eq(5L), eq(itemDto))).thenReturn(ResponseEntity.of(Optional.of(itemDto)));
        this.mvc
                .perform(post(URL)
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable().toString()), String.class));
    }

    @Test
    void testDeleteItem() throws Exception {
        this.mvc
                .perform(delete(URL + "/5")
                        .header("X-Sharer-User-Id", "5"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testPatchItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(itemDto);
        when(client.patch(eq(5L), eq(5), eq(itemDto))).thenReturn(ResponseEntity.of(Optional.of(itemDto)));
        this.mvc
                .perform(patch(URL + "/5")
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testGetIdItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(client.getId(eq(5L), eq(5L))).thenReturn(ResponseEntity.of(Optional.of(itemDto)));
        this.mvc
                .perform(get(URL + "/5")
                        .header("X-Sharer-User-Id", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable().toString()), String.class));
    }

    @Test
    void testGetSearch() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(5L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(client.getSearch(eq("ame"), anyInt(), anyInt())).thenReturn(ResponseEntity.of(Optional.of(itemDto)));
        this.mvc
                .perform(get(URL + "/search?text=ame"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable().toString()), String.class));
    }

    @Test
    void testPostComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(2);
        commentDto.setText("text");

        String json = mapper.writeValueAsString(commentDto);
        when(client.postComment(eq(5L), eq(5L), eq(commentDto)))
                .thenReturn(ResponseEntity.of(Optional.of(commentDto)));
        this.mvc
                .perform(post(URL + "/5/comment")
                        .header("X-Sharer-User-Id", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class));
    }
}