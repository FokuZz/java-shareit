package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> get() {
        return userService.getList();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable long userId,
                         @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @PostMapping
    public UserDto post(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}
