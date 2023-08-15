package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long id;

    @NotBlank(message = "description can't be blank")
    @Size(max = 255, message = "description length should not be more than 255 characters")
    String description;

    LocalDateTime created;
}
