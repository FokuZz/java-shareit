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
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testItemSerialization() throws Exception {
        long id = 1L;
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        long bookerId = 5L;
        long itemId = 2L;
        Status status = Status.WAITING;
        BookingDto bookingDto = new BookingDto(id, start, end, itemId, bookerId, status);
        JsonContent<BookingDto> result = json.write(bookingDto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
        assertThat(result).extractingJsonPathValue("$.bookerId").isEqualTo((int) bookerId);
        assertThat(result).extractingJsonPathValue("$.itemId").isEqualTo((int) itemId);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.toString());
    }

}