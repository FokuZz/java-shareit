package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoObjects;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingDao bookingDao;
    @InjectMocks
    BookingServiceImpl service;
    @Mock
    private UserDao userDao;
    @Mock
    private ItemDao itemDao;
    private User owner;
    private User booker;

    private User user;
    private Item item;
    private Booking booking;
    private Booking booking2;

    @BeforeEach
    void setup() {
        LocalDateTime start = LocalDateTime.now().plusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(5);
        owner = new User();
        owner.setName("artem");
        owner.setEmail("artemka@mail.ru");
        owner.setId(1L);

        booker = new User();
        booker.setName("igor");
        booker.setEmail("krytoy111@mail.ru");
        booker.setId(2L);

        user = new User();
        user.setName("vika");
        user.setEmail("vikusic@mail.ru");
        user.setId(3L);

        item = new Item();
        item.setId(1L);
        item.setName("Пылесос");
        item.setDescription("кому нужен пылесос?");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(start.plusSeconds(10));
        booking2.setEnd(end.plusSeconds(10));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(Status.APPROVED);
    }


    @Test
    void testCreateBookingFailOwner() {
        long ownerId = owner.getId();
        long itemId = item.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingDto bookingToSave = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
        when(userDao.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        String error = String.format("User с id %d владелец вещи с id %d", ownerId, itemId);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.createBooking(ownerId, bookingToSave));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testCreateBookingFailAvailable() {
        long itemId = item.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingDto bookingToSave = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
        item.setAvailable(false);
        long bookerId = booker.getId();
        when(userDao.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        String error = "Item available = false: забронировать вещь можно только когда она доступна";
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createBooking(bookerId, bookingToSave));
        assertEquals(error, ex.getMessage());
    }

    @Test
    void testCreateBookingFailStartIsNull() {
        long itemId = item.getId();
        LocalDateTime end = booking.getEnd();
        BookingDto bookingToSave = BookingDto.builder()
                .itemId(itemId)
                .start(null)
                .end(end)
                .build();
        item.setAvailable(false);
        long bookerId = booker.getId();
        String error = "В теле Booking отсутствует старт/конец";
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.createBooking(bookerId, bookingToSave));
        assertEquals(error, ex.getMessage());
    }

    @Test
    void testCreateBookingFailItemIdIsNull() {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingDto bookingToSave = BookingDto.builder()
                .itemId(null)
                .start(start)
                .end(end)
                .build();
        item.setAvailable(false);
        long bookerId = booker.getId();
        String error = "Отсутствует itemId";
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.createBooking(bookerId, bookingToSave));
        assertEquals(error, ex.getMessage());
    }

    @Test
    void testCreateBookingFailData() {
        long itemId = item.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingDto bookingToSave = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
        item.setAvailable(true);
        long bookerId = booker.getId();
        when(userDao.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingDao.isAvailableTime(itemId, booking.getStart(), booking.getEnd()))
                .thenReturn(List.of(booking2));
        String error = "Время для бронирования не доступно";
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.createBooking(bookerId, bookingToSave));
        assertEquals(error, ex.getMessage());
    }


    @Test
    void testCreateBookingStandard() {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        long itemId = item.getId();
        long bookerId = booker.getId();
        BookingDto bookingToSave = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(userDao.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingDao.isAvailableTime(itemId, booking.getStart(), booking.getEnd()))
                .thenReturn(Collections.emptyList());
        when(bookingDao.save(any())).thenReturn(booking);
        BookingDtoObjects bookingOutDto = service.createBooking(bookerId, bookingToSave);

        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }


    @Test
    void testCreateBookingFailStartInBefore() {
        LocalDateTime start = booking.getStart().minusDays(1);
        LocalDateTime end = booking.getEnd();
        long itemId = item.getId();
        long bookerId = booker.getId();
        BookingDto bookingToSave = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(userDao.findById(bookerId)).thenReturn(Optional.of(booker));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createBooking(bookerId, bookingToSave)
        );
    }

    @Test
    void testConfirmationFailStatusAlreadyTrue() {
        long userId = owner.getId();
        long bookingId = booking.getId();
        when(bookingDao.getBookingWithAll(bookingId)).thenReturn(Optional.of(booking));
        String error = String.format("Бронирование с id %d уже подтверждено", bookingId);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.confirmation(userId, true, bookingId)
        );
        assertEquals(error, exception.getMessage());

    }

    @Test
    void testConfirmationStandardFalse() {
        long userId = owner.getId();
        long bookingId = booking.getId();
        when(bookingDao.getBookingWithAll(bookingId)).thenReturn(Optional.of(booking));
        when(bookingDao.save(any())).thenReturn(booking);
        BookingDtoObjects bookingOutDto = service.confirmation(userId, false, bookingId);
        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }

    @Test
    void testConfirmationStandardTrue() {
        long userId = owner.getId();
        long bookingId = booking.getId();
        Booking bookingTest = booking;
        bookingTest.setStatus(Status.WAITING);
        when(bookingDao.getBookingWithAll(bookingId)).thenReturn(Optional.of(bookingTest));
        when(bookingDao.save(any())).thenReturn(bookingTest);
        BookingDtoObjects bookingOutDto = service.confirmation(userId, true, bookingId);
        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }

    @Test
    void testConfirmationFail() {
        long userId = owner.getId();
        long bookingId = booking.getId();
        Booking bookingTest = booking;
        bookingTest.setStatus(Status.REJECTED);
        when(bookingDao.getBookingWithAll(bookingId)).thenReturn(Optional.of(bookingTest));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.confirmation(userId, false, bookingId)
        );
        assertEquals("Бронирование с id 1 уже отклонено", exception.getMessage());
    }

    @Test
    void testConfirmationFailNotFound() {
        long userId = owner.getId();
        long bookingId = booking.getId();
        Booking bookingTest = booking;
        bookingTest.setStatus(Status.REJECTED);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.confirmation(userId, false, bookingId)
        );
        assertEquals("Booking не был найден по id = 1", exception.getMessage());
    }

    @Test
    void testGetOnlyOwnerOrBookerStandardStandard() {
        long userId = owner.getId();
        long bookerId = booker.getId();
        long bookingId = booking.getId();
        when(bookingDao.getByOwnerIdOrBookerId(userId, bookingId)).thenReturn(Optional.of(booking));
        BookingDtoObjects bookingOutDto = service.getOnlyOwnerOrBooker(userId, bookingId);
        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());

        when(bookingDao.getByOwnerIdOrBookerId(bookerId, bookingId)).thenReturn(Optional.of(booking));
        bookingOutDto = service.getOnlyOwnerOrBooker(bookerId, bookingId);
        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }

    @Test
    void testGetOnlyOwnerOrBookerStandardFail() {
        long userId = user.getId();
        long bookingId = booking.getId();
        String error = "Бронирование не найдено, возможно вы не её владелец/создатель";
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.getOnlyOwnerOrBooker(userId, bookingId)
        );
        assertEquals(error, exception.getMessage());
    }


    @Test
    void testGetListOfUserBookerFailWrongState() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));

        String error = "Unknown state: UNSUPPORTED_STATUS";
        UnsupportedStatusException exception = assertThrows(
                UnsupportedStatusException.class,
                () -> service.getListOfUserBooker(userId, State.UNKNOWN, from, size)
        );
        assertEquals(error, exception.getMessage());

    }

    @Test
    void testGetListOfUserBookerAll() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        PageRequest page = PageRequest.of(0, size, Sort.Direction.DESC, "start");
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findAllByBookerIdOrderByStartDesc(userId, page)).thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoObjects> bookingOutDtos = service.getListOfUserBooker(userId, State.ALL, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());
        assertEquals(booking.getId(), bookingOutDtos.get(0).getId());

    }

    @Test
    void testGetListOfUserBookerPast() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListOfUserBooker(userId, State.PAST, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListOfUserBookerCurrent() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        booking.setEnd(LocalDateTime.now().plusSeconds(120));
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListOfUserBooker(userId, State.CURRENT, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListOfUserBookerFuture() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        booking.setStart(LocalDateTime.now().plusSeconds(60));
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListOfUserBooker(userId, State.FUTURE, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListOfUserBookerWaiting() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        booking.setStatus(Status.WAITING);
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListOfUserBooker(userId, State.WAITING, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListOfUserBookerRejected() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        booking.setStatus(Status.REJECTED);
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByBookerIdAndStatusOrderByStartDesc(eq(userId), eq(Status.REJECTED), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDtoObjects> bookingOutDtos = service.getListOfUserBooker(userId, State.REJECTED, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListBookerOfOwnerItemsFailWrongState() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        String error = "Unknown state: UNSUPPORTED_STATUS";
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        UnsupportedStatusException exception = assertThrows(
                UnsupportedStatusException.class,
                () -> service.getListBookerOfOwnerItems(userId, State.UNKNOWN, from, size)
        );
        assertEquals(error, exception.getMessage());

    }

    @Test
    void testGetListBookerOfOwnerItemsAll() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        PageRequest page = PageRequest.of(0, size, Sort.Direction.DESC, "start");
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByItemOwnerIdOrderByStartDesc(userId, page)).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListBookerOfOwnerItems(userId, State.ALL, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());
        assertEquals(booking.getId(), bookingOutDtos.get(0).getId());

    }

    @Test
    void testGetListBookerOfOwnerItemsPast() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListBookerOfOwnerItems(userId, State.PAST, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListBookerOfOwnerItemsCurrent() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        booking.setEnd(LocalDateTime.now().plusSeconds(120));
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListBookerOfOwnerItems(userId, State.CURRENT, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListBookerOfOwnerItemsFuture() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        booking.setStart(LocalDateTime.now().plusSeconds(60));
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListBookerOfOwnerItems(userId, State.FUTURE, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListBookerOfOwnerItemsWaiting() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        booking.setStatus(Status.WAITING);
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListBookerOfOwnerItems(userId, State.WAITING, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());

    }

    @Test
    void testGetListBookerOfOwnerItemsRejected() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        booking.setStatus(Status.REJECTED);
        when(userDao.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingDao.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDtoObjects> bookingOutDtos = service.getListBookerOfOwnerItems(userId, State.REJECTED, from, size);

        assertNotNull(bookingOutDtos);
        assertEquals(1, bookingOutDtos.size());
    }

}
