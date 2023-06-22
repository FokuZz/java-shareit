package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemDao repository;

    private final UserDao userDao;
    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        return repository.getByUserId(userId);
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if(itemDto.getName() == null || itemDto.getName().isBlank()
                || itemDto.getDescription() == null || itemDto.getDescription().isBlank()
                || itemDto.getAvailable() == null) {
            throw new ValidationException("Нельзя создавать пользователя без поля name/email");
        }
        userDao.getById(userId);
        return repository.save(itemDto, userId);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        repository.deleteByUserIdAndItemId(userId,itemId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userDao.getById(userId);
        return repository.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return repository.getItem(itemId);
    }

    @Override
    public List<ItemDto> getAll() {
        return repository.getAll();
    }

    @Override
    public List<ItemDto> searchByText(String text, long userId) {
        return repository.searchByText(text, userId);
    }
}
