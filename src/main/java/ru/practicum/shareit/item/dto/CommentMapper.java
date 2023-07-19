package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class CommentMapper {
    public static Comment mapToComment(CommentDto commentDto, User user, Item item ){
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .author(user)
                .item(item)
                .build();
    }

    public static CommentDto mapToCommentDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .itemId(comment.getItem().getId())
                .build();
    }

    public static List<CommentDto> mapToCommentDto(Iterable<Comment> comments){
        List<CommentDto> commentDtos = new ArrayList<>();
        for(Comment comment: comments){
            commentDtos.add(mapToCommentDto(comment));
        }
        return commentDtos;
    }

}
