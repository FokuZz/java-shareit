package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {
    public static UserDto toUserMapper(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
    public static User rsToUser(ResultSet rs) throws SQLException {
        return new User(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email")
        );
    }
}
