package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingDao {
    List<Booking> getAll();

    Booking getById(Long bookingId);

    Booking create(Booking booking);

    void deleteById(Long bookingId);
}
