package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface CommentDao extends JpaRepository<Comment, Long> {

    List<Comment> findByItem(Item item);
}
