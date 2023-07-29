package ru.practicum.shareit.item.dto;

import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.QComment;
import ru.practicum.shareit.item.QItem;
import ru.practicum.shareit.user.QUser;

import static org.junit.jupiter.api.Assertions.*;

public class QCommentTest {

    @Test
    public void testQCommentFields() {
        QComment qComment = QComment.comment;
        assertTrue(qComment instanceof EntityPathBase<?>);
        assertNotNull(qComment.author);
        assertTrue(qComment.author instanceof QUser);
        assertNotNull(qComment.created);
        assertTrue(qComment.created instanceof DateTimePath<?>);
        assertNotNull(qComment.id);
        assertTrue(qComment.id instanceof NumberPath<?>);
        assertNotNull(qComment.item);
        assertTrue(qComment.item instanceof QItem);
        assertNotNull(qComment.text);
        assertTrue(qComment.text instanceof StringPath);
    }

    @Test
    public void testQCommentConstructorWithVariable() {
        String variable = "myVariable";

        QComment qComment = new QComment(variable);
        assertNotNull(qComment);
        assertFalse(qComment.getMetadata().getName().equalsIgnoreCase("comment"));
    }
}
