package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId()
        );
    }
    public static Item rsToItem(ResultSet rs) throws SQLException {
        return new Item(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("available"),
                rs.getLong("ownerId"),
                rs.getLong("requestId")
        );
    }
}
