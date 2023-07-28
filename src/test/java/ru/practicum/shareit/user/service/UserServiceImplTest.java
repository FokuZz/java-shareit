package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("name");
        user.setEmail("e@mail.ru");
        user.setId(1L);
    }

    @Test
    void testGetListEmpty() {
        when(userDao.findAll()).thenReturn(Collections.emptyList());
        List<UserDto> users = service.getList();
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void testGetListOneUser() {
        when(userDao.findAll()).thenReturn(List.of(user));
        List<UserDto> users = service.getList();
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void testCreateRegular() {
        User userToSave = new User();
        userToSave.setName("name");
        userToSave.setEmail("e@mail.ru");

        when(userDao.save(any())).thenReturn(user);
        UserDto userDto = UserMapper.mapToUserDto(userToSave);
        UserDto userSaved = service.create(userDto);

        assertNotNull(userSaved);
        assertEquals(user.getId(), userSaved.getId());
        verify(userDao, times(1)).save(any());
    }

    @Test
    void testCreateFailNullEmail() {
        User userToSave = new User();
        userToSave.setName("name");
        userToSave.setEmail("e@mail.ru");
        UserDto userDto = UserMapper.mapToUserDto(userToSave);
        String email = user.getEmail();
        String error = String.format("Пользователь с email %s уже существует", email);
        when(userDao.save(any())).thenThrow(new RuntimeException("Пользователь с email e@mail.ru уже существует"));
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.create(userDto)
        );
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testDeleteStandard() {
        long userId = 1L;
        service.deleteById(userId);
        verify(userDao, times(1)).deleteById(userId);
    }

    @Test
    void testGetUserByIdFailNotFound() {
        long userIdNotFound = 0L;
        String error = String.format("Пользователь не найден по id = %d", userIdNotFound);
        when(userDao.findById(userIdNotFound)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getUserById(userIdNotFound));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testGetUserByIdStandard() {
        long userId = user.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        UserDto userFound = service.getUserById(userId);
        assertNotNull(userFound);
        assertEquals(userId, userFound.getId());
    }

    @Test
    void testPatchUserFailNotFoundUser() {
        long userIdNotFound = 0L;
        when(userDao.findById(userIdNotFound)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getUserById(userIdNotFound));
        assertEquals(String.format("Пользователь не найден по id = %d", userIdNotFound), exception.getMessage());
    }

    @Test
    void testPatchUserStandardName() {
        long userId = user.getId();
        String nameUpdated = "nameUpdated";
        User userUpdated = new User();
        userUpdated.setId(userId);
        userUpdated.setName(nameUpdated);
        userUpdated.setEmail(user.getEmail());
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any())).thenReturn(userUpdated);
        UserDto userDtoUpdated = service.updateUser(userId, UserDto.builder().name(nameUpdated).build());

        assertNotNull(userDtoUpdated);
        assertEquals(userId, userDtoUpdated.getId());
        assertEquals(nameUpdated, userDtoUpdated.getName());
    }

    @Test
    void testPatchUserStandardEmail() {
        long userId = user.getId();
        String emailUpdated = "updated@mail.ru";
        User userUpdated = new User();
        userUpdated.setId(userId);
        userUpdated.setEmail(emailUpdated);
        userUpdated.setName(user.getName());
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any())).thenReturn(userUpdated);
        UserDto userDtoUpdated = service.updateUser(userId, UserDto.builder().email(emailUpdated).build());

        assertNotNull(userDtoUpdated);
        assertEquals(userId, userDtoUpdated.getId());
        assertEquals(emailUpdated, userDtoUpdated.getEmail());
    }

    @Test
    void testPatchUserFailBlankName() {
        long userId = user.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any())).thenReturn(user);
        userDao.save(user);
        UserDto userNew = UserDto.builder()
                .id(userId)
                .email(user.getEmail())
                .name("")
                .build();
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.updateUser(userId, userNew)
        );
        assertEquals("Поле name не может быть пустым", ex.getMessage());
    }

    @Test
    void testPatchUserFailBlankEmail() {
        long userId = user.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any())).thenReturn(user);
        userDao.save(user);
        UserDto userNew = UserDto.builder()
                .id(userId)
                .email("")
                .name(user.getName())
                .build();
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.updateUser(userId, userNew)
        );
        assertEquals("Поле email не может быть пустым", ex.getMessage());
    }


}