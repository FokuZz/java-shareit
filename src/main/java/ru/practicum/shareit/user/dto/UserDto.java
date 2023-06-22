package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class UserDto {
    long id;

    String name;

    @Email
    String email;

    public UserDto(long id, @NotBlank String name, @Email String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return name.equals(userDto.name) && email.equals(userDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
