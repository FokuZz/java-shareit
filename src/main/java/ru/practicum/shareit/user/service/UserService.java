package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getList();
    UserDto create(UserDto userDto, long userId);

    void deleteById(long userId);
}
