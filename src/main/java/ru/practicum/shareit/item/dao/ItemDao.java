package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemDao extends JpaRepository<Item, Long> {

    List<Item> findByItemId(Long userId);

    void deleteByItemIdAndUserId(Long userId, Long itemId);

    @Query("SELECT i " +
            "FROM Item i " +
            "join fetch i.owner as o " +
            "WHERE i.id = ?1 " +
            "and o.id = ?2")
    Optional<Item> getWithUserIdAndItemId(Long userId, Long itemId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.id = ?2" +
            "AND (LOWER(i.name) LIKE '%?1%' " +
            "OR LOWER(i.description) LIKE '%?1%')")
    List<Item> getListILikeByText(String text, Long itemId);
}
