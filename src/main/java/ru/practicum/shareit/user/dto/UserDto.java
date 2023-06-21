package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class UserDto {
    String name;
    String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
