package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> get() {
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        return userClient.getId(userId);
    }

    @PatchMapping("/{userId}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<Object> patch(@PathVariable long userId,
                                        @RequestBody @Valid UserDto userDto) {
        return userClient.patch(userId, userDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<Object> post(@RequestBody @Valid UserDto userDto) {
        return userClient.post(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        return userClient.delete(userId);
    }
}
