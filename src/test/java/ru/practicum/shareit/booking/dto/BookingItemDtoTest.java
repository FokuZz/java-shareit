package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingItemDtoTest {

    @Autowired
    private JacksonTester<BookingItemDto> json;

    @Test
    void testBookingItemDtoTest() throws Exception {
        long id = 1L;
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        long bookerId = 5L;
        Status status = Status.WAITING;
        BookingItemDto bookingItemDto = new BookingItemDto(id, start, end, bookerId, status);
        JsonContent<BookingItemDto> result = json.write(bookingItemDto);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathValue("$.bookerId").isEqualTo((int) bookerId);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.toString());
    }

}