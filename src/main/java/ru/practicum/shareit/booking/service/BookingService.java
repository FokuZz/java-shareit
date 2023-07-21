package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoObjects;

import java.util.List;

public interface BookingService {
    BookingDtoObjects createBooking(Long userId, BookingDto bookingDto);


    BookingDtoObjects confirmation(Long userId, boolean approved, Long bookingId);


    BookingDtoObjects getOnlyOwnerOrBooker(Long userId, Long bookingId);

    List<BookingDtoObjects> getListOfUserBooker(Long userId, State state);

    List<BookingDtoObjects> getListBookerOfOwnerItems(Long userId, State state);

}
