package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoObjectsTest {
    @Autowired
    private JacksonTester<BookingDtoObjects> json;

    @Test
    void testBookingItemDtoTest() throws Exception {
        long id = 1L;
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        ItemDto itemDto = ItemDto.builder()
                .id(3L)
                .name("Дрель")
                .description("Очень мощная")
                .available(true)
                .requestId(5L)
                .build();
        UserDto userDto = UserDto.builder()
                .id(2L)
                .name("Artem")
                .email("Artemka@email.com")
                .build();
        Status status = Status.WAITING;
        BookingDtoObjects bookingDtoObjects = new BookingDtoObjects(id, start, end, itemDto, userDto, status);
        JsonContent<BookingDtoObjects> result = json.write(bookingDtoObjects);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.toString());
    }
}