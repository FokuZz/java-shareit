package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserDao {
    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto create(UserDto userDto);

    void deleteById(Long userId);

    UserDto updateUser(long userId, UserDto userDto);
}
