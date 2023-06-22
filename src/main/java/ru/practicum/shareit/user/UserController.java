package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping
    public List<UserDto> get(){
        return userService.getList();
    }

    @PostMapping
    public UserDto post(@RequestHeader("X-Later-User-Id") long userId,
                     @RequestBody UserDto userDto){
        return userService.create(userDto, userId);
    }

    @DeleteMapping
    public void delete(@RequestHeader("X-Later-User-Id") long userId){
        userService.deleteById(userId);
    }
}
