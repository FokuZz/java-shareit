package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.rsToItem;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemDaoImpl implements ItemDao {
    @Override
    public List<Item> getByUserId(long userId) {
//        String sql = "SELECT * FROM items WHERE user_id = ?";
//        return jdbcTemplate.query(sql,(rs, rowNum) -> rsToItem(rs), userId);
        return null;
    }

    @Override
    public List<Item> getAll() {
//        String sql = "SELECT * FROM items WHERE user_id = ?";
//        return jdbcTemplate.query(sql,(rs, rowNum) -> rsToItem(rs), userId);
        return null;
    }

    @Override
    public Item save(Item item) {
//        String sql = "INSERT INTO items (id,user_id,url) VALUES(?,?,?)";
//        if (jdbcTemplate.update(sql,item.getId(),item.getOwnerId(),item.get()) != 1){
//            log.error("Не получилось создать Item = {}",item);
//            throw new RuntimeException();
//        }
//        log.warn("Создание предмета Item = {}", item);
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
//        String sql = "DELETE from items WHERE user_id = ? AND id = ?";
//        if(jdbcTemplate.update(sql,userId,itemId) != 1){
//            log.error("Удаления по параметрам userId = {}, itemId = {} не произошло",userId,itemId);
//            throw new RuntimeException();
//        }
//        log.warn("Произошло удаление по параметрам userId = {}, itemId = {}",userId,itemId);
    }
}