package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Optional<Booking> getBookingWithAll(Long bookingId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "JOIN i.owner o " +
            "JOIN b.booker br " +
            "WHERE (o.id = ?1 OR br.id = ?1) " +
            "AND b.id = ?2")
    Optional<Booking> getByOwnerIdOrBookerId(Long ownerId, Long bookingId);


    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);


    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    List<Booking> findByBookerIdAndItemIdAndStatusAndStartIsBefore(
            Long userId, long itemId, Status status, LocalDateTime now);

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long user, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Long user, Pageable pageable);

    List<Booking> findByItemId(Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE (b.item.id = ?1) " +
            "AND (b.status = 'APPROVED') " +
            "AND (b.start between ?2 and ?3 " +
            "OR b.end between ?2 and ?3 " +
            "OR b.start <= ?2 AND b.end >= ?3)")
    List<Booking> isAvailableTime(long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdAndStatusOrStatusOrderByStartAsc(Long id, Status status1, Status status2);

    Page<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long id, LocalDateTime time, Pageable page);

    Page<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long id, LocalDateTime time, Pageable page);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime start,
                                                                              LocalDateTime end,
                                                                              Pageable page);

    List<Booking> findByBookerIdAndStatus(Long id, Status status);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long itemOwnerId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end,
                                                                                 Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time, Pageable pageable);

    List<Booking> findByItemIdInAndStatusOrStatusOrderByStartAsc(List<Long> itemIds, Status approved, Status waiting);
}
