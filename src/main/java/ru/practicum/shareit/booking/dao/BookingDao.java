package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.util.List;
import java.util.Optional;

public interface BookingDao extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdAndBookerId(Long aLong, Long bookerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "JOIN b.booker br " +
            "WHERE (o.id = ?1 OR br.id = ?1) " +
            "AND b.id = ?2")
    Optional<Booking> getByOwnerIdOrBookerId(Long userId, Long bookingId);


    List<Booking> findByItem_IdAndStatus(Long userId, Status status);

    List<Booking> findByBooker_IdAndStatus(Long userId, Status status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.booker br " +
            "WHERE br.id = ?1")
    List<Booking> getAllByBookerId(Long user);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "WHERE o.id = ?1")
    List<Booking> getAllByItemOwnerId(Long user);

    List<Booking> findByItemId(Long itemId);
}
