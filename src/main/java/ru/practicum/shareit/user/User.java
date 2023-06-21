package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class User {
    long id;

    String name;

    String email;

    public User(long id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
