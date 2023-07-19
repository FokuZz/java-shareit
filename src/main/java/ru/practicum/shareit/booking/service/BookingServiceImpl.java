package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;
    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        log.info("Попытка создания booking = {} userId = {}", bookingDto, userId);
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingDao.save(BookingMapper.mapToBooking(
                bookingDto,
                itemDao.findById(bookingDto.getItemId())
                        .orElseThrow(() -> new NotFoundException(
                                "Предмет не найден по id = " + bookingDto.getItemId())),
                userDao.findById(userId)
                        .orElseThrow(() -> new NotFoundException(
                                "Пользователь не найден по id = " + userId
                        ))));
        log.info("Всё выполнилось успешно booking = {}", booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto confirmation(Long userId, boolean approved, Long bookingId) {
        log.info("Попытка подтверждения с userId = {}, approved = {}, bookingId = {}", userId, approved, bookingId);
        Booking booking = bookingDao.findByIdAndBookerId(userId, bookingId)
                .orElseThrow(() -> new NotFoundException("Booking не был найдет по id = " + userId));
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingDao.save(booking);
        log.info("Все выполнилось успешно booking = {}", booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getOnlyOwnerOrBooker(Long userId, Long bookingId) {
        log.info("Попытка получение владельцем или создателем брони по userId = {} bookingId = {}", userId, bookingId);
        Booking booking = bookingDao.getByOwnerIdOrBookerId(userId, bookingId)
                .orElseThrow(() -> new NotFoundException(
                        "Бронирование не найдено, возможно вы не её владелец/создатель"));
        log.info("Все выполнилось успешно booking = {}", booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getListOfUserBooker(Long userId, State state) {
        log.info("Попытка получения списка id бронирований создателя по статусу userId = {} state = {}"
                , userId, state);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingDao.getAllByBookerId(userId);
                break;
            case CURRENT:
                bookings = bookingDao.findByBookerIdAndBookerStatus(userId, Status.APPROVED);
                break;
            case PAST:
                bookings = bookingDao.findByBookerIdAndBookerStatus(userId, Status.REJECTED);
                bookings.addAll(bookingDao.findByBookerIdAndBookerStatus(userId, Status.CANCELED));
                break;
            case FUTURE:
                bookings = bookingDao.findByBookerIdAndBookerStatus(userId, Status.WAITING);
                bookings.addAll(bookingDao.findByBookerIdAndBookerStatus(userId, Status.APPROVED));
                break;
            case WAITING:
                bookings = bookingDao.findByBookerIdAndBookerStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingDao.findByBookerIdAndBookerStatus(userId, Status.REJECTED);
                break;
            default:
                throw new RuntimeException("Неверный state = " + state);
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public List<BookingDto> getListBookerOfOwnerItems(Long userId, State state) {
        log.info("Попытка получения списка id бронирований пользователя предметов по статусу userId = {} state = {}"
                , userId, state);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingDao.getAllByItemOwnerId(userId);
                break;
            case CURRENT:
                bookings = bookingDao.findByItemIdAndBookerStatus(userId, Status.APPROVED);
                break;
            case PAST:
                bookings = bookingDao.findByItemIdAndBookerStatus(userId, Status.REJECTED);
                bookings.addAll(bookingDao.findByItemIdAndBookerStatus(userId, Status.CANCELED));
                break;
            case FUTURE:
                bookings = bookingDao.findByItemIdAndBookerStatus(userId, Status.WAITING);
                bookings.addAll(bookingDao.findByItemIdAndBookerStatus(userId, Status.APPROVED));
                break;
            case WAITING:
                bookings = bookingDao.findByItemIdAndBookerStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingDao.findByItemIdAndBookerStatus(userId, Status.REJECTED);
                break;
            default:
                throw new RuntimeException("Неверный state = " + state);
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

}
