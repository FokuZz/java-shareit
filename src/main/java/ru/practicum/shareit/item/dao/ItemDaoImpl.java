package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.rsToItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemDaoImpl implements ItemDao {
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ItemDto> getByUserId(long userId) {
        String sql = "SELECT * FROM items WHERE user_id = ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> toItemDto(rsToItem(rs)), userId);
    }

    @Override
    public List<ItemDto> getAll() {
//        String sql = "SELECT * FROM items WHERE user_id = ?";
//        return jdbcTemplate.query(sql,(rs, rowNum) -> rsToItem(rs), userId);
        return null;
    }

    @Override
    public ItemDto save(Item item) {
        String sql = "INSERT INTO items (id,name,description, available, ownerId, requestId) VALUES(?,?,?,?,?,?)";
        if (jdbcTemplate.update(sql,item.getId(),item.getName(),item.getDescription(),
                item.isAvailable(), item.getOwnerId(), item.getRequestId()) != 1){
            log.error("Не получилось создать Item = {}",item);
            throw new RuntimeException();
        }
        log.warn("Создание предмета Item = {}", item);
        return toItemDto(item);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        String sql = "DELETE from items WHERE owner_id = ? AND id = ?";
        if(jdbcTemplate.update(sql,userId,itemId) != 1){
            log.error("Удаления по параметрам userId = {}, itemId = {} не произошло",userId,itemId);
            throw new RuntimeException();
        }
        log.warn("Произошло удаление по параметрам userId = {}, itemId = {}",userId,itemId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId) {
        String sql = "UPDATE users"
        if(jdbcTemplate.update(sql,userId,itemId) != 1){
            log.error("Удаления по параметрам userId = {}, itemId = {} не произошло",userId,itemId);
            throw new RuntimeException();
        }
        log.warn("Произошло удаление по параметрам userId = {}, itemId = {}",userId,itemId);
        return null;
    }

    @Override
    public ItemDto getItem(long itemId) {
        return null;
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        return null;
    }
}