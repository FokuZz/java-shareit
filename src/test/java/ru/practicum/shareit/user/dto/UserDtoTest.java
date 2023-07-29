package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testEqualsAndHashCode() {
        User user = new User();
        user.setId(1L);
        User user2 = new User();
        user2.setId(5L);
        User user3 = new User();
        user3.setId(1L);
        UserDto userDto = UserDto.builder()
                .id(1L)
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(5L)
                .build();
        UserDto userDto3 = UserDto.builder()
                .id(1L)
                .build();

        assertEquals(user, user3);
        assertNotEquals(user, user2);
        assertEquals(user.hashCode(), user3.hashCode());

        assertEquals(userDto, userDto3);
        assertNotEquals(userDto, userDto2);
        assertEquals(userDto.hashCode(), userDto3.hashCode());
    }

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "Artem",
                "artemka@yandex.ru"
        );

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Artem");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("artemka@yandex.ru");
    }
}