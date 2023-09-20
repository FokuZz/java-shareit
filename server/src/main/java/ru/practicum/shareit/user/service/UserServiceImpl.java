package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExist;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<UserDto> getList() {
        log.info("Попытка поиска всех пользователей");
        return UserMapper.mapToUserDto(userDao.findAll());
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Попытка создания пользователя по UserDto = {}", userDto);
        User user = UserMapper.mapToUser(userDto);
        User userAdded;
        try {
            userAdded = userDao.save(user);
        } catch (RuntimeException e) {
            throw new AlreadyExist(e.getMessage());
        }
        return UserMapper.mapToUserDto(userAdded);
    }

    @Override
    public void deleteById(long userId) {
        log.info("Попытка удаления по userId = {}", userId);
        userDao.deleteById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Попытка получения пользователя по userId = {}", userId);
        return UserMapper.mapToUserDto(userDao.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден по id = " + userId)
        ));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        log.info("Попытка обновления пользователя по userId = {} на UserDto = {}", userId, userDto);
        User user = userDao.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден по id = " + userId));
        boolean isHasName = userDto.getName() != null;
        boolean isHasEmail = userDto.getEmail() != null;

        if (isHasName) {
            if (userDto.getName().isBlank()) throw new ValidationException("Поле name не может быть пустым");
            user.setName(userDto.getName());
        }
        if (isHasEmail) {
            if (userDto.getEmail().isBlank()) throw new ValidationException("Поле email не может быть пустым");
            user.setEmail(userDto.getEmail());
        }

        log.info("Обновление user = {}", user);
        return UserMapper.mapToUserDto(userDao.save(user));
    }
}
