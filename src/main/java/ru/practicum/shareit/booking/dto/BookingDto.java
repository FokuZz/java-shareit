package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class BookingDto {

    LocalDateTime start;

    LocalDateTime end;

    Long itemId;

    Long bookerId;

    Status status;

    public BookingDto(LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId, Status status) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
        this.bookerId = bookerId;
        this.status = status;
    }
}
