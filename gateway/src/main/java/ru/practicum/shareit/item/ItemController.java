package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false)
            @PositiveOrZero(message = "Нельзя вводить отрицательные число")
            int from,
            @RequestParam(name = "size", defaultValue = "10", required = false)
            @Positive(message = "Нельзя вводить отрицательные число")
            int size) {
        log.info("GetItems with userid={}, from={}, size={}", userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Post Item = {}, userId={}", itemDto, userId);
        return itemClient.postItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId) {
        log.info("Delete userId={}, itemId={}", userId, itemId);
        return itemClient.delete(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Patch itemId={}, userId={}, Item={}", itemId, userId, itemDto);
        return itemClient.patch(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getId(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long itemId) {
        log.info("GetById userId={}, itemId={}", userId, itemId);
        return itemClient.getId(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestParam(defaultValue = "0", required = false)
            @Min(value = 0, message = "Нельзя вводить отрицательные число")
            int from,
            @RequestParam(defaultValue = "10", required = false)
            @Positive(message = "Нельзя вводить отрицательные число")
            int size) {
        log.info("GetSearch text={}, from={}, size={}", text, from, size);
        return itemClient.getSearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        return itemClient.postComment(userId, itemId, commentDto);
    }
}
