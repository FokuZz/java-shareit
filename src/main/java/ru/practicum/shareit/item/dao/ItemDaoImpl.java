package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.rsToItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemDaoImpl implements ItemDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ItemDto> getByUserId(long userId) {
        String sql = "SELECT * FROM items WHERE OWNER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> toItemDto(rsToItem(rs)), userId);
    }

    @Override
    public List<ItemDto> getAll() {
        String sql = "SELECT * FROM items";
        return jdbcTemplate.query(sql, (rs, rowNum) -> toItemDto(rsToItem(rs)));
    }

    @Override
    public ItemDto save(ItemDto itemDto, long userId) {
        String sql = "INSERT INTO items (name,description, available, OWNER_ID) VALUES(?,?,?,?)";
        if (jdbcTemplate.update(sql, itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), userId) != 1) {
            log.error("Не получилось создать Item = {}", itemDto);
            throw new RuntimeException();
        }
        log.warn("Создание предмета Item = {}", itemDto);
        return getItemByName(itemDto);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        String sql = "DELETE from items WHERE owner_id = ? AND id = ?";
        if (jdbcTemplate.update(sql, userId, itemId) != 1) {
            log.error("Удаления по параметрам userId = {}, itemId = {} не произошло", userId, itemId);
            throw new RuntimeException();
        }
        log.warn("Произошло удаление по параметрам userId = {}, itemId = {}", userId, itemId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        try {
            String sql = "SELECT * FROM ITEMS WHERE ID = ? AND OWNER_ID = ?";
            Item item = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rsToItem(rs), itemId, userId);

            boolean isHasNewName = itemDto.getName() != null;
            boolean isHasNewDescription = itemDto.getDescription() != null;
            boolean isAvailable = itemDto.getAvailable() != null;

            if (isHasNewName) {
                item.setName(itemDto.getName());
            }
            if (isHasNewDescription) {
                item.setDescription(itemDto.getDescription());
            }
            if (isAvailable) {
                item.setAvailable(itemDto.getAvailable());
            }

            sql = "UPDATE ITEMS SET NAME = ?, DESCRIPTION = ?, AVAILABLE = ? WHERE ID = ? AND OWNER_ID = ?";
            if (jdbcTemplate.update(sql, item.getName(), item.getDescription(), item.isAvailable(), itemId, userId) != 1) {
                log.error("Обновления по параметрам userId = {}, itemId = {} не произошло", userId, itemId);
                throw new RuntimeException();
            }
            log.warn("Произошло обновление по параметрам userId = {}, itemId = {}", userId, itemId);
            return toItemDto(item);
        } catch (EmptyResultDataAccessException e) { // На случай если предмет не того юзера
            log.error("Предмета itemId {} пользователя id {} не найдено", itemId, userId);
            throw new NotFoundException(String.format("Предмета с id %s не найдено", itemId));
        }
    }

    @Override
    public ItemDto getItem(long itemId) {
        String sql = "SELECT * FROM ITEMS WHERE ID = ?";
        try {
            Item item = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rsToItem(rs), itemId);
            log.info("Предмет найден: {}", item);
            return toItemDto(item);
        } catch (EmptyResultDataAccessException e) {
            log.error("Предмета с id {} не найдено", itemId);
            throw new NotFoundException(String.format("Предмета с id %s не найдено", itemId));
        }
    }

    @Override
    public List<ItemDto> searchByText(String text, long userId) {
        text = "'%" + text.toLowerCase().replace("_", "\\_").replace("%", "\\%") + "%'";
        String sql = "SELECT * FROM ITEMS WHERE OWNER_ID = ? AND LOWER(NAME) LIKE " + text +
                " OR LOWER(DESCRIPTION) LIKE " + text;
        List<ItemDto> itemDtos = jdbcTemplate.query(sql, (rs, rowNum) -> toItemDto(rsToItem(rs)), userId);
        log.info("Число найденных предметов: {}", itemDtos.size());
        return itemDtos;
    }


    private ItemDto getItemByName(ItemDto itemDto) {
        String sql = "SELECT * FROM ITEMS WHERE NAME = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> toItemDto(rsToItem(rs)), itemDto.getName());
    }

}