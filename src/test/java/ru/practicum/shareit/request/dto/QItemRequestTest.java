package ru.practicum.shareit.request.dto;

import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.QItemRequest;
import ru.practicum.shareit.user.QUser;

import static org.junit.jupiter.api.Assertions.*;

public class QItemRequestTest {

    @Test
    public void testQItemRequestFields() {
        QItemRequest qItemRequest = QItemRequest.itemRequest;
        assertTrue(qItemRequest instanceof EntityPathBase<?>);
        assertNotNull(qItemRequest.created);
        assertTrue(qItemRequest.created instanceof DateTimePath<?>);
        assertNotNull(qItemRequest.description);
        assertTrue(qItemRequest.description instanceof StringPath);
        assertNotNull(qItemRequest.id);
        assertTrue(qItemRequest.id instanceof NumberPath<?>);
        assertNotNull(qItemRequest.requestor);
        assertTrue(qItemRequest.requestor instanceof QUser);
    }

    @Test
    public void testQItemRequestConstructorWithVariable() {
        String variable = "myVariable";
        QItemRequest qItemRequest = new QItemRequest(variable);
        assertNotNull(qItemRequest);
        assertFalse(qItemRequest.getMetadata().getName().equalsIgnoreCase("itemRequest"));
    }

}
