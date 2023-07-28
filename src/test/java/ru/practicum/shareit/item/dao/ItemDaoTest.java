package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemDaoTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemDao repository;

    @Autowired
    private UserDao userRepo;

    @Autowired
    private ItemRequestDao requestRepo;

    private User owner;
    private Item item;
    private User requestor;
    private ItemRequest request;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner = userRepo.save(owner);

        requestor = new User();
        requestor.setName("name1");
        requestor.setEmail("e1@mail.ru");
        requestor = userRepo.save(requestor);

        request = new ItemRequest();
        request.setDescription("description");
        request.setRequestor(requestor);
        request = requestRepo.save(request);

        item = new Item();
        item.setName("Набор отверток");
        item.setDescription("Большой набор отверток");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
        item = repository.save(item);
    }

    @Test
    void testFindByIdFetch() {
        Item itemTest = repository.findByIdFetch(item.getId()).orElse(null);
        assertNotNull(itemTest);
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testFindItemsByOwnerId() {
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size);
        List<Item> items = repository.findItemsByOwnerId(owner.getId(), page).getContent();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void testDeleteByIdAndOwnerId() {
        repository.deleteByIdAndOwnerId(item.getId(), owner.getId());
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size);
        List<Item> items = repository.findItemsByOwnerId(owner.getId(), page).getContent();
        assertEquals(0, items.size());
        Item itemTest = repository.findById(item.getId()).orElse(null);
        assertNull(itemTest);
    }

    @Test
    void testFindByIdAndOwnerId() {
        Item itemTest = repository.findByIdAndOwnerId(item.getId(), owner.getId()).orElse(null);
        assertNotNull(itemTest);
        assertEquals(item, itemTest);
    }


    @Test
    void testGetListILikeByText() {
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size);
        List<Item> items = repository.getListILikeByText("отВерток", page).getContent();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void testFindByRequestIdIn() {
        List<Item> items = repository.findByRequestIdIn(List.of(request.getId()));
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void findByRequestId() {
        List<Item> items = repository.findByRequestId(request.getId());
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }
}