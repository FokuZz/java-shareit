package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class BookItemRequestDto {

    @NotNull(message = "itemId can't be NULL")
    long itemId;
    @NotNull(message = "start can't be NULL")
    @FutureOrPresent(message = "start can't be in PAST")
    LocalDateTime start;

    @NotNull(message = "end can't be NULL")
    @Future(message = "end can't be in PAST")
    LocalDateTime end;
}