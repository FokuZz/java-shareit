package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemDao {
    List<ItemDto> getAll();

    List<ItemDto> getByUserId(long itemId);

    ItemDto save(ItemDto itemDto, long userId);

    void deleteByUserIdAndItemId(long userId, long itemId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItem(long itemId);

    List<ItemDto> searchByText(String text, long userId);
}
