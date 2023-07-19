package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDto bookingDto);


    BookingDto confirmation(Long userId, boolean approved, Long bookingId);


    BookingDto getOnlyOwnerOrBooker(Long userId, Long bookingId);

    List<BookingDto> getListOfUserBooker(Long userId, State state);

    List<BookingDto> getListBookerOfOwnerItems(Long userId, State state);

}
