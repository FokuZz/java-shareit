package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class BookingDtoItem {

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long bookerId;
    Status status;
}
