package ru.practicum.shareit.booking.dto;

import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.QBooking;
import ru.practicum.shareit.item.QItem;
import ru.practicum.shareit.user.QUser;

import static org.junit.jupiter.api.Assertions.*;

public class QBookingTest {

    @Test
    public void testQBookingFields() {
        QBooking qBooking = QBooking.booking;
        assertTrue(qBooking instanceof EntityPathBase<?>);
        assertNotNull(qBooking.booker);
        assertTrue(qBooking.booker instanceof QUser);
        assertNotNull(qBooking.end);
        assertTrue(qBooking.end instanceof DateTimePath<?>);
        assertNotNull(qBooking.id);
        assertTrue(qBooking.id instanceof NumberPath<?>);
        assertNotNull(qBooking.item);
        assertTrue(qBooking.item instanceof QItem);
        assertNotNull(qBooking.start);
        assertTrue(qBooking.start instanceof DateTimePath<?>);
        assertNotNull(qBooking.status);
        assertTrue(qBooking.status instanceof EnumPath<?>);
    }

    @Test
    public void testQBookingConstructorWithVariable() {
        String variable = "booking";
        QBooking qBooking = new QBooking(variable);
        assertNotNull(qBooking);
        assertEquals("booking", qBooking.getMetadata().getName());
    }
}