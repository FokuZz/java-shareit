package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ItemDao itemDao;

    private Booking booking;
    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner = userDao.save(owner);

        booker = new User();
        booker.setName("name1");
        booker.setEmail("e1@mail.ru");
        booker = userDao.save(booker);

        item = new Item();
        item.setName("Набор отверток");
        item.setDescription("Большой набор отверток");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemDao.save(item);

        LocalDateTime now = LocalDateTime.now();
        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.plusSeconds(5));
        booking.setEnd(now.plusSeconds(60));
        booking.setStatus(Status.APPROVED);
        booking = bookingDao.save(booking);
    }

    @Test
    void testGetBookerWithAll() {
        long bookingId = booking.getId();
        Booking bookingTest = bookingDao.getBookingWithAll(bookingId).orElse(null);
        assertNotNull(booking);
        assertEquals(booking, bookingTest);
    }

    @Test
    void testGetByOwnerIdOrBookerId() {
        long ownerId = owner.getId();
        long bookingId = booking.getId();
        Booking bookingTest = bookingDao.getByOwnerIdOrBookerId(ownerId, bookingId).orElse(null);
        assertNotNull(booking);
        assertEquals(booking, bookingTest);
    }

    @Test
    void testIsAvailableTime() {
        LocalDateTime start = booking.getStart().plusSeconds(5);
        LocalDateTime end = booking.getEnd().plusSeconds(25);
        long itemId = item.getId();
        List<Booking> bookings = bookingDao.isAvailableTime(itemId, start, end);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }
}