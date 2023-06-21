package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemDao {
    List<Item> getAll();

    List<Item> getByUserId(long itemId);

    Item save(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);
}
