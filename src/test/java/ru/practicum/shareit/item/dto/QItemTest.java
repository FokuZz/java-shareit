package ru.practicum.shareit.item.dto;

import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.QItem;
import ru.practicum.shareit.request.QItemRequest;
import ru.practicum.shareit.user.QUser;

import static org.junit.jupiter.api.Assertions.*;

public class QItemTest {

    @Test
    public void testQItemFields() {
        QItem qItem = QItem.item;
        assertTrue(qItem instanceof EntityPathBase<?>);
        assertNotNull(qItem.available);
        assertTrue(qItem.available instanceof BooleanPath);
        assertNotNull(qItem.description);
        assertTrue(qItem.description instanceof StringPath);
        assertNotNull(qItem.id);
        assertTrue(qItem.id instanceof NumberPath<?>);
        assertNotNull(qItem.name);
        assertTrue(qItem.name instanceof StringPath);
        assertNotNull(qItem.owner);
        assertTrue(qItem.owner instanceof QUser);
        assertNotNull(qItem.request);
        assertTrue(qItem.request instanceof QItemRequest);
    }

    @Test
    public void testQItemConstructorWithVariable() {
        String variable = "myVariable";
        QItem qItem = new QItem(variable);
        assertNotNull(qItem);
        assertFalse(qItem.getMetadata().getName().equalsIgnoreCase("item"));
    }
}
