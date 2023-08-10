package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long id;

    @NotBlank(message = "Поле description не может быть пустым")
    String description;

    Long requestorId;

    LocalDateTime created;

}
