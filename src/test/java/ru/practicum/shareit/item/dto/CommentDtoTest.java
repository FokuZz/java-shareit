package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Comment;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;


    @Test
    void testComments() {
        CommentDto commentDto = CommentDto.builder()
                .text("hello")
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .text("bye")
                .build();
        CommentDto commentDto3 = CommentDto.builder()
                .text("hello")
                .build();
        Comment comment = new Comment();
        comment.setText("hello");
        Comment comment2 = new Comment();
        comment2.setText("bye");
        Comment comment3 = new Comment();
        comment3.setText("hello");

        assertEquals(comment, comment3);
        assertEquals(comment.hashCode(), comment3.hashCode());
        assertNotEquals(comment, comment2);

        assertEquals(commentDto, commentDto3);
        assertEquals(commentDto.hashCode(), commentDto3.hashCode());
        assertNotEquals(commentDto, commentDto2);
    }

    @Test
    void testCommentSerialization() throws Exception {
        long id = 1L;
        String text = "text";
        String authorName = "name";

        LocalDateTime created = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(id, text, authorName, created);
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(authorName);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(text);
        assertThat(result).extractingJsonPathValue("$.created").isNotNull();

    }

}