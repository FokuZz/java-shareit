package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
class ItemWithCommentDtoTest {

    @Autowired
    private JacksonTester<ItemWithCommentDto> json;

    @Test
    void testItem() throws Exception {
        ItemWithCommentDto item = ItemWithCommentDto.builder()
                .name("Aboba")
                .description("Done")
                .id(1L)
                .build();
        ItemWithCommentDto item2 = ItemWithCommentDto.builder()
                .name("Aboba")
                .description("Done")
                .id(1L)
                .build();
        ItemWithCommentDto item3 = ItemWithCommentDto.builder()
                .name("Victor")
                .description("Done")
                .id(2L)
                .build();
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
        BookingItemDto bookingItemDto = BookingItemDto.builder()
                .id(4L)
                .build();
        BookingItemDto bookingItemDto2 = BookingItemDto.builder()
                .id(5L)
                .build();
        ItemWithCommentDto itemTest = ItemWithCommentDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .lastBooking(bookingItemDto)
                .nextBooking(bookingItemDto2)
                .build();
        JsonContent<ItemWithCommentDto> result = json.write(itemTest);
        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(name);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(description);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(available);
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(4);
        assertThat(result).extractingJsonPathValue("$.nextBooking.id").isEqualTo(5);

    }
}