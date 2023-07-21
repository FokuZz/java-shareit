package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingDao extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner o " +
            "WHERE b.id = ?1")
    Optional<Booking> getBookerWithAll(Long bookerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "JOIN b.booker br " +
            "WHERE (o.id = ?1 OR br.id = ?1) " +
            "AND b.id = ?2")
    Optional<Booking> getByOwnerIdOrBookerId(Long userId, Long bookingId);


    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status);


    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findByBookerIdAndItemIdAndStatusAndStartIsBefore(
            Long userId, long itemId, Status status, LocalDateTime now);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long user);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long user);

    List<Booking> findByItemId(Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE (b.item.id = ?1) " +
            "AND (b.status = 'APPROVED') " +
            "AND (b.start between ?2 and ?3 " +
            "OR b.end between ?2 and ?3 " +
            "OR b.start <= ?2 AND b.end >= ?3)")
    List<Booking> isAvailbleTime(long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdAndStatusOrStatusOrderByStartAsc(Long id, Status status1, Status status2);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long booker_id, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStatus(Long id, Status status);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long item_owner_id, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByItemIdInAndStatusOrStatusOrderByStartAsc(List<Long> itemIds, Status approved, Status waiting);
}
