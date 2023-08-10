package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String URL = "/items";
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService service;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDto.ItemDtoBuilder itemDtoBuilder;
    private ItemWithCommentDto.ItemWithCommentDtoBuilder itemWithCommentDtoBuilder;
    private CommentDto.CommentDtoBuilder commentDtoBuilder;

    @BeforeEach
    void setupBuilder() {
        itemDtoBuilder = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true);
        itemWithCommentDtoBuilder = ItemWithCommentDto.builder()
                .name("name")
                .description("description")
                .available(true);
        commentDtoBuilder = CommentDto.builder()
                .text("comment");
    }

    @Test
    void testGetItemsByUserIdEmpty() throws Exception {
        when(service.getItemsByUserId(1L, 0, 10)).thenReturn(new ArrayList<>());
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetItemsByUserIdOneObject() throws Exception {
        ItemWithCommentDto itemDto = itemWithCommentDtoBuilder.id(1L).build();
        when(service.getItemsByUserId(1L, 0, 1)).thenReturn(List.of(itemDto));
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class));
    }

    @Test
    void testGetItemsByUserIdFailEmptyHeader() throws Exception {
        String error = "X-Sharer-User-Id";
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(error)));
    }

    @Test
    void testCreateStandard() throws Exception {
        long userId = 1L;
        itemDto = itemDtoBuilder.build();
        ItemDto itemDtoAdded = itemDtoBuilder.id(1L).build();

        String json = mapper.writeValueAsString(itemDto);
        String jsonAdded = mapper.writeValueAsString(itemDtoAdded);

        when(service.create(userId, itemDto)).thenReturn(itemDtoAdded);
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void testCreateFailEmptyHeader() throws Exception {
        String json = mapper.writeValueAsString(itemDto);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("X-Sharer-User-Id")));

    }

    @Test
    void testDeleteStandard() throws Exception {
        mvc.perform(delete(URL + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateItemName() throws Exception {
        String json = "{\"name\": \"namePatched\"}";
        mvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateItemDescription() throws Exception {
        String json = "{\"description\": \"descriptionPatched\"}";
        mvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void testUpdateItemAvailable() throws Exception {
        String json = "{\"available\": \"false\"}";
        mvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testGetItem() throws Exception {
        ItemDto itemDto = itemDtoBuilder.id(1L).build();
        String json = mapper.writeValueAsString(itemDto);

        when(service.updateItem(1, 1, itemDto)).thenReturn(itemDto);
        mvc.perform(get(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testSearchByTextEmpty() throws Exception {

        when(service.searchByText("", 0, 10)).thenReturn(new ArrayList<>());
        mvc
                .perform(get(URL + "/search")
                        .param("text", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void testSearchByText() throws Exception {
        ItemDto itemDto = itemDtoBuilder.id(1L).name("Отвертка").build();
        when(service.searchByText("ОтВ", 0, 1)).thenReturn(List.of(itemDto));
        mvc.perform(get(URL + "/search")
                        .param("text", "ОтВ")
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class));
    }

    @Test
    void testCreateComment() throws Exception {
        CommentDto commentDto = commentDtoBuilder.build();
        String jsonIn = mapper.writeValueAsString(commentDto);
        CommentDto commentDtoOut = commentDtoBuilder
                .id(1L)
                .authorName("name")
                .created(LocalDateTime.now())
                .build();
        String json = mapper.writeValueAsString(commentDtoOut);
        when(service.createComment(1L, 1L, commentDto)).thenReturn(commentDtoOut);
        mvc.perform(post(URL + "/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonIn))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

}