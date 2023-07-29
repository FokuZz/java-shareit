package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;


    @Test
    void testItem() throws Exception {
        Item item = new Item();
        item.setName("Aboba");
        item.setId(1L);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(6L);
        item.setRequest(itemRequest);
        Item item2 = new Item();
        item2.setName("Victor");
        item2.setId(1L);
        Item item3 = new Item();
        item3.setId(2L);
        item3.setName("Alena");
        User owner = new User();
        owner.setId(1L);
        owner.setName("abobas");
        Item itemMap = ItemMapper.mapToItem(ItemMapper.mapToItemDto(item), owner);
        assertNotNull(itemMap.getRequest());
        assertEquals(item, item2);
        assertNotEquals(item, item3);
        assertEquals(item.hashCode(), item2.hashCode());
    }

    @Test
    void testItemSerialization() throws Exception {
        long id = 1L;
        String name = "name";
        String description = "description";
        boolean available = true;
        long requestId = 1L;
        ItemDto itemDto = new ItemDto(id, name, description, available, requestId);
        ItemDto itemDto2 = new ItemDto(id, "boba", "description", available, requestId);
        ItemDto itemDto3 = new ItemDto(id, "name", "description", available, requestId);

        JsonContent<ItemDto> result = json.write(itemDto);
        assertEquals(itemDto, itemDto3);
        assertNotEquals(itemDto, itemDto2);
        assertEquals(itemDto.hashCode(), itemDto3.hashCode());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(available);
    }
}