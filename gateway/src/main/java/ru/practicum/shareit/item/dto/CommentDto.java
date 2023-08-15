package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    long id;
    @NotBlank(message = "text can't be BLANK")
    @Size(max = 255, message = "text length should not be more than 255 characters")
    String text;
    String authorName;
    LocalDateTime created;
}
