package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class CommentDto {
    long id;
    String text;
    String authorName;
    LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDto commentDto = (CommentDto) o;
        return text.equals(commentDto.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
