package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemRequest {
    @NonNull
    long id;

    String description;

    Long requestorId;

    LocalDateTime created;
}