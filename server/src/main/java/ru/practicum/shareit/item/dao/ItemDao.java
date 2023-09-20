package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao extends JpaRepository<Item, Long>, CrudRepository<Item, Long>,
        PagingAndSortingRepository<Item, Long> {

    @Query("SELECT i " +
            "FROM Item i " +
            "JOIN FETCH i.owner o " +
            "WHERE i.id = ?1")
    Optional<Item> findByIdFetch(Long itemId);

    Page<Item> findItemsByOwnerId(Long userId, Pageable page);

    void deleteByIdAndOwnerId(Long itemId, Long ownerId);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long userId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE CONCAT('%', LOWER(?1), '%') " +
            "OR LOWER(i.description) LIKE CONCAT('%', LOWER(?1), '%'))")
    Page<Item> getListILikeByText(String text, Pageable page);

    List<Item> findByRequestIdIn(List<Long> requestIds);

    List<Item> findByRequestId(Long requestId);

}
