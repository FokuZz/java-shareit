package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item mapToItem(ItemDto itemDto, User owner){
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static ItemDto mapToItemDto(Item item){
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public static List<ItemDto> mapToItemDto(Iterable<Item> items){
        List<ItemDto> itemDtos = new ArrayList<>();
        for(Item item : items){
            itemDtos.add(mapToItemDto(item));
        }
        return itemDtos;
    }

    public static ItemWithCommentDto mapToItemWitchCommentDto(Item item, Booking next,
                                                              Booking last, List<Comment> comments){
        return ItemWithCommentDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .nextBooking(BookingMapper.mapToBookingDto(next))
                .lastBooking(BookingMapper.mapToBookingDto(last))
                .comments(CommentMapper.mapToCommentDto(comments))
                .build();
    }

}
