package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentDaoTest {

    @Autowired
    private CommentDao repository;

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private UserDao userDao;

    private User owner;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setup() {
        User author = new User();
        author.setName("name");
        author.setEmail("e@mail.ru");

        owner = new User();
        owner.setName("name2");
        owner.setEmail("e2@mail.ru");

        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);

        comment = new Comment();
        comment.setText("comment");
        comment.setItem(item);
        comment.setAuthor(author);

        author = userDao.save(author);
        owner = userDao.save(owner);
        itemDao.save(item);
        repository.save(comment);
    }

    @Test
    void testFindAllByItemIdOrderByCreatedDescEmpty() {
        List<Comment> comments = repository.findAllByItemIdOrderByCreatedDesc(99L);
        assertNotNull(comments);
        assertEquals(0, comments.size());
    }

    @Test
    void testFindAllByItemIdOrderByCreatedDescOneObject() {
        List<Comment> comments = repository.findAllByItemIdOrderByCreatedDesc(comment.getItem().getId());
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comments.get(0).getId(), comment.getId());
    }

    @Test
    void testFindAllByItemIdInOrderByCreatedDescEmpty() {
        Item item1 = new Item();
        item1.setOwner(owner);
        item1.setAvailable(true);
        item1.setName("name1");
        item1.setDescription("description1");
        itemDao.save(item1);

        List<Comment> comments = repository.findAllByItemIdInOrderByCreatedDesc(List.of(item1.getId()));
        assertNotNull(comments);
        assertEquals(0, comments.size());
    }

    @Test
    void testFindAllByItemIdInOrderByCreatedDescOneObject() {
        List<Comment> comments = repository.findAllByItemIdInOrderByCreatedDesc(List.of(item.getId()));
        assertNotNull(comments);
        assertEquals(1, comments.size());
    }
}