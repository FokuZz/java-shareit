package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class Booking {

    long id;

    LocalDateTime start;

    LocalDateTime end;

    Long itemId;

    Long bookerId;

    Status status;
}
