package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> get(@RequestHeader("X-Later-User-Id") long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Later-User-Id") Long userId,
                    @RequestBody Item item) {
        return itemService.create(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteByUserIdAndItemId(userId, itemId);
    }
}
