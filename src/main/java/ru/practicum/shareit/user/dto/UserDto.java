package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class UserDto {
    Long id;

    String name;

    @Email
    String email;
}
