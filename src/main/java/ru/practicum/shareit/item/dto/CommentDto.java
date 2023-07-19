package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class CommentDto {
    @Id
    long id;
    String text;
    Long itemId;
    Long authorId;
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
