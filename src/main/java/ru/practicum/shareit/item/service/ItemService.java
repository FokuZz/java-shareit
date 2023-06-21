package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItemsByUserId(long userId);

    Item create(long userId, Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);
}
