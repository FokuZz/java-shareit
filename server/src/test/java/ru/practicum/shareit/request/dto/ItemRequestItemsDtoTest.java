package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
class ItemRequestItemsDtoTest {

    @Autowired
    private JacksonTester<ItemRequestItemsDto> json;


    @Test
    void testEqualsAndHashCode() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setId(1L);

        assertEquals(itemRequest, itemRequest3);
        assertNotEquals(itemRequest, itemRequest2);
        assertEquals(itemRequest.hashCode(), itemRequest3.hashCode());
    }

    @Test
    void testRequestSerialization() throws Exception {
        long id = 1L;
        String description = "description";
        LocalDateTime created = LocalDateTime.now();
        List<ItemDto> items;

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(id)
                .build();
        items = List.of(itemDto);

        ItemRequestItemsDto itemRequestDto =
                new ItemRequestItemsDto(id, description, created);
        itemRequestDto.addAllItems(items);
        JsonContent<ItemRequestItemsDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();

    }
}