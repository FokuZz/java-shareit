package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Comment;

import java.util.List;
import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class ItemWithCommentDto {
    long id;

    String name;

    String description;

    Boolean available;

    BookingDto lastBooking;

    BookingDto nextBooking;

    List<CommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemWithCommentDto itemWithCommentDto = (ItemWithCommentDto) o;
        return name.equals(itemWithCommentDto.name) && description.equals(itemWithCommentDto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
