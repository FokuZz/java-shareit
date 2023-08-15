package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Post userId={}, ItemRequest={}", userId, itemRequestDto);
        return itemRequestClient.post(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemsReq(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get userId={}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0")
            @Min(value = 0, message = "Нельзя вводить отрицательные число")
            Integer from,
            @RequestParam(name = "size", defaultValue = "10")
            @Positive(message = "Нельзя вводить отрицательные число")
            Integer size) {
        log.info("GetAllItems userId={}, from={}, size{}", userId, from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("requestId") Long requestId) {
        log.info("Get userId={}, requestId={}", userId, requestId);
        return itemRequestClient.getId(userId, requestId);
    }
}
