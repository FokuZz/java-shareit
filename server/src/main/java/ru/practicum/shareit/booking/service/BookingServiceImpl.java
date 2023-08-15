package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoObjects;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;
    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    @Transactional
    public BookingDtoObjects createBooking(Long userId, BookingDto bookingDto) {
        log.info("Попытка создания booking = {} userId = {}", bookingDto, userId);
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) throw new ValidationException(
                "В теле Booking отсутствует старт/конец");
        if (bookingDto.getItemId() == null) throw new ValidationException("Отсутствует itemId");
        User booker = getUser(userId);
        long itemId = bookingDto.getItemId();
        Item item = getItem(itemId);
        bookingDto.setStatus(Status.WAITING);

        if (isOwner(userId, item)) throw new NotFoundException(String.format(
                "User с id %d владелец вещи с id %d", userId, itemId));

        if (!item.isAvailable()) throw new IllegalArgumentException(
                "Item available = false: забронировать вещь можно только когда она доступна");
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) throw new ValidationException(
                "Дата окончания бронирования должна быть после даты начала");

        List<Booking> bookings = bookingDao.isAvailableTime(itemId, bookingDto.getStart(), bookingDto.getEnd());

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Значение Start при аренде не может быть в прошлом");
        }
        if (!bookings.isEmpty()) {
            throw new ValidationException("Время для бронирования не доступно");
        }

        Booking booking = bookingDao.save(BookingMapper.mapToBooking(
                bookingDto,
                item,
                booker));

        log.info("Всё выполнилось успешно booking = {}", booking);
        return BookingMapper.mapToBookingDtoOut(booking);
    }

    @Override
    @Transactional
    public BookingDtoObjects confirmation(Long userId, boolean approved, Long bookingId) {
        log.info("Попытка подтверждения с userId = {}, approved = {}, bookingId = {}", userId, approved, bookingId);
        Booking booking = bookingDao.getBookingWithAll(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking не был найден по id = " + bookingId));
        if (!booking.getItem().getOwner().getId().equals(userId)) throw new NotFoundException(
                "User id не является владельцем вещи");
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException(String.format("Бронирование с id %d уже подтверждено", bookingId));
            }
            booking.setStatus(Status.APPROVED);
        } else {
            if (booking.getStatus().equals(Status.REJECTED)) {
                throw new ValidationException(String.format("Бронирование с id %d уже отклонено", bookingId));
            }
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingDao.save(booking);
        log.info("Все выполнилось успешно booking = {}", booking);
        return BookingMapper.mapToBookingDtoOut(booking);
    }

    @Override
    public BookingDtoObjects getOnlyOwnerOrBooker(Long userId, Long bookingId) {
        log.info("Попытка получение владельцем или создателем брони по userId = {} bookingId = {}", userId, bookingId);
        Booking booking = bookingDao.getByOwnerIdOrBookerId(userId, bookingId)
                .orElseThrow(() -> new NotFoundException(
                        "Бронирование не найдено, возможно вы не её владелец/создатель"));
        log.info("Все выполнилось успешно booking = {}", booking);
        return BookingMapper.mapToBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoObjects> getListOfUserBooker(Long userId, State state, int from, int size) {
        log.info("Попытка получения списка id бронирований создателя по статусу userId = {} state = {}",
                userId, state);
        PageRequest page = PageRequest.of(from / size, size, Sort.Direction.DESC, "start");
        LocalDateTime time = LocalDateTime.now();
        getUser(userId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingDao.findAllByBookerIdOrderByStartDesc(userId, page).getContent();
                break;
            case CURRENT:
                bookings = bookingDao.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, time,
                        time, page).getContent();
                break;
            case PAST:
                bookings = bookingDao.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, time, page).getContent();
                break;
            case FUTURE:
                bookings = bookingDao.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, time, page).getContent();
                break;
            case WAITING:
                bookings = bookingDao.findByBookerIdAndStatusOrderByStartDesc(userId,
                        Status.WAITING, page).getContent();
                break;
            case REJECTED:
                bookings = bookingDao.findByBookerIdAndStatusOrderByStartDesc(userId,
                        Status.REJECTED, page).getContent();
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoOut(bookings);
    }

    @Override
    public List<BookingDtoObjects> getListBookerOfOwnerItems(Long userId, State state, int from, int size) {
        log.info("Попытка получения списка id бронирований пользователя предметов по статусу userId = {} state = {}",
                userId, state);
        PageRequest page = PageRequest.of(from / size, size, Sort.Direction.DESC, "start");
        LocalDateTime time = LocalDateTime.now();
        getUser(userId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingDao.findByItemOwnerIdOrderByStartDesc(userId, page).getContent();
                break;
            case CURRENT:
                bookings = bookingDao.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, time,
                        time, page).getContent();
                break;
            case PAST:
                bookings = bookingDao.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, time, page).getContent();
                break;
            case FUTURE:
                bookings = bookingDao.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, time, page).getContent();
                break;
            case WAITING:
                bookings = bookingDao.findByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        Status.WAITING, page).getContent();
                break;
            case REJECTED:
                bookings = bookingDao.findByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        Status.REJECTED, page).getContent();
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoOut(bookings);
    }

    private User getUser(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User с id = %d не найден", userId)));
    }

    private Item getItem(Long itemId) {
        return itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item с id = %d не найден", itemId)));
    }

    private boolean isOwner(long userId, Item item) {
        long ownerId = item.getOwner().getId();
        return ownerId == userId;
    }
}
