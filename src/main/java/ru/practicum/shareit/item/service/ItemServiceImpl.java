package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemDao repository;
    @Override
    public List<Item> getItemsByUserId(long userId) {
        return repository.getByUserId(userId);
    }

    @Override
    public Item create(long userId, Item item) {
        item.setOwnerId(userId);
        return repository.save(item);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        repository.deleteByUserIdAndItemId(userId,itemId);
    }
}
