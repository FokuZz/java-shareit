package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Later-User-Id") long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Later-User-Id") Long userId,
                    @RequestBody Item item) {
        return itemService.create(userId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteByUserIdAndItemId(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Later-User-Id") long userId,
                              @PathVariable long itemId){
        return itemService.updateItem(userId,itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId){
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text){
        if(text.isEmpty()){
           return itemService.getAll();
        }
        return itemService.searchByText(text);
    }
}
