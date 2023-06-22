package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExist;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<UserDto> getList() {
        log.info("Попытка поиска всех пользователей");
        return userDao.getAll();
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Попытка создания пользователя по UserDto = {}", userDto);
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new ValidationException("Нельзя создавать пользователя без поля name/email");
        }
        repeatCheck(userDto);
        return userDao.create(userDto);
    }

    @Override
    public void deleteById(long userId) {
        userDao.deleteById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        return userDao.getById(userId);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        log.info("Попытка обновления пользователя по userId = {} на UserDto = {}", userId, userDto);
        repeatCheckWithId(userDto, userId);
        return userDao.updateUser(userId, userDto);
    }

    private void repeatCheck(UserDto userDto) {
        List<UserDto> userDtos = userDao.getAll();
        userDtos.forEach(user -> {
            if (user.getName().equals(userDto.getName()) || user.getEmail().equals(userDto.getEmail())) {
                throw new AlreadyExist(String.format("Пользователь %s уже существует", userDto));
            }
        });
    }

    private void repeatCheckWithId(UserDto userDto, long userId) {
        List<UserDto> userDtos = userDao.getAll();
        userDtos.forEach(user -> {
            if (user.getName().equals(userDto.getName()) || user.getEmail().equals(userDto.getEmail())
                    && user.getId() != userId) {
                throw new AlreadyExist(String.format("Пользователь %s уже существует", userDto));
            }
        });
    }
}
