package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestDao {
    List<ItemRequest> getAll();

    ItemRequest getById(Long itemRequestId);

    ItemRequest create(ItemRequest itemRequest);

    void deleteById(Long itemRequestId);
}
