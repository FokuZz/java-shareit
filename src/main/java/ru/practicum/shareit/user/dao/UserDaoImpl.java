package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.rsToUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserMapper;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDaoImpl implements UserDao {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<UserDto> getAll() {
        String sql = "SELECT * FROM users";
       return jdbcTemplate.query(sql, (rs, rowNum) ->  toUserMapper(rsToUser(rs)));
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
            throw new UserNotFoundException(String.format("Пользователь с id {} не найден", userId));
        }
    }

    @Override
    public void deleteById(Long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public UserDto create(User user) {
        String sql = "INSERT INTO users (id, name, email) VALUES(?,?,?)";
        if (jdbcTemplate.update(sql,user.getId(),user.getName(),user.getEmail()) != 1){
            log.error("Не получилось создать юзера User = {}",user);
            throw new RuntimeException();
        }
        log.warn("Создание пользователя User = {}", user);
        return toUserMapper(user);
    }
}
