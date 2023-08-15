package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static ru.practicum.shareit.validation.ValidationGroups.Create;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class UserDto {
    Long id;

    @NotBlank(groups = Create.class, message = "name can't be blank")
    String name;

    @Email(message = "email must be EMAIL format")
    @NotBlank(groups = Create.class, message = "email can't be blank")
    String email;
}
