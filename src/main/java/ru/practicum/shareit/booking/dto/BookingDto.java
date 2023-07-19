package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class BookingDto {

    Long id;

    LocalDateTime start;

    LocalDateTime end;

    Long itemId;

    Long bookerId;

    Status status;

}
