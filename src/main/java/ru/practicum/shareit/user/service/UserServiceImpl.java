package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private UserDao userDao;

    @Override
    public List<UserDto> getList() {
        return userDao.getAll();
    }

    @Override
    public UserDto create(UserDto userDto, long userId){

        return userDao.create(new User(userId, userDto.getName(), userDto.getEmail()));
    }

    @Override
    public void deleteById(long userId) {
        userDao.deleteById(userId);
    }
}
