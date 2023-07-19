package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {


}
