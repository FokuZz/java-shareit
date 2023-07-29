package ru.practicum.shareit.user.dto;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.QUser;

import static org.junit.jupiter.api.Assertions.*;

public class QUserTest {

    @Test
    public void testQUserFields() {
        QUser qUser = QUser.user;
        assertTrue(qUser instanceof EntityPathBase<?>);
        assertNotNull(qUser.email);
        assertTrue(qUser.email instanceof StringPath);
        assertNotNull(qUser.id);
        assertTrue(qUser.id instanceof NumberPath<?>);
        assertNotNull(qUser.name);
        assertTrue(qUser.name instanceof StringPath);
    }

    @Test
    public void testQUserConstructorWithVariable() {
        String variable = "user";
        QUser qUser = new QUser(variable);
        assertNotNull(qUser);
        assertEquals("user", qUser.getMetadata().getName());
    }
}

