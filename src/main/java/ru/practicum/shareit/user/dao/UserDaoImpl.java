package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.rsToUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserMapper;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<UserDto> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> toUserMapper(rsToUser(rs)));
    }

    @Override
    public UserDto getById(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            UserDto userDto = toUserMapper(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rsToUser(rs), userId));
            log.info("Пользователь найден: {}", userDto);
            return userDto;
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователь с id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id {} не найден", userId));
        }
    }

    @Override
    public void deleteById(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        String sql = "SELECT * FROM USERS WHERE ID = ?";
        User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rsToUser(rs), userId);

        boolean isHasNewName = userDto.getName() != null;
        boolean isHasNewEmail = userDto.getEmail() != null;

        if (isHasNewName) {
            user.setName(userDto.getName());
        }
        if (isHasNewEmail) {
            user.setEmail(userDto.getEmail());
        }

        sql = "UPDATE USERS SET NAME = ?, EMAIL = ? WHERE ID = ? ";
        if (jdbcTemplate.update(sql, user.getName(), user.getEmail(), userId) != 1) {
            log.error("Обновления по параметрам userId = {} не произошло", userId);
            throw new RuntimeException();
        }
        log.warn("Произошло обновление по параметрам userId = {}", userId);
        return toUserMapper(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        String sql = "INSERT INTO users (name, email) VALUES(?,?)";
        if (jdbcTemplate.update(sql, userDto.getName(), userDto.getEmail()) != 1) {
            log.error("Не получилось создать юзера UserDto = {}", userDto);
            throw new RuntimeException();
        }
        log.warn("Создание пользователя UserDto = {}", userDto);
        return getByName(userDto.getName());
    }

    private UserDto getByName(String name) {
        String sql = "SELECT * FROM users WHERE NAME = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> toUserMapper(rsToUser(rs)), name);
    }
}
