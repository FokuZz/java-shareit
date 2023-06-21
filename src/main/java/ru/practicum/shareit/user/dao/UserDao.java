package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserDao {
    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto create(User user);

    void deleteById(Long userId);

}
