package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemDao repository;
    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        return repository.getByUserId(userId);
    }

    @Override
    public ItemDto create(long userId, Item item) {
        item.setOwnerId(userId);
        return repository.save(item);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        repository.deleteByUserIdAndItemId(userId,itemId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId) {
        return repository.updateItem(userId, itemId);
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
    public List<ItemDto> searchByText(String text) {
        return repository.searchByText(text);
    }
}
