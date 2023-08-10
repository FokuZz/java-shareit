package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestDaoTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestDao itemRequestDao;
    private ItemRequest request;
    private User requestor;

    @BeforeEach
    void setup() {
        requestor = new User();
        requestor.setName("artem");
        requestor.setEmail("artemka@mail.ru");
        request = new ItemRequest();
        request.setDescription("description");
        request.setRequestor(requestor);
    }

    @Test
    void testFindByRequestorIdEmpty() {
        List<ItemRequest> requests = itemRequestDao.findByRequestorId(1L);
        assertNotNull(requests);
        assertEquals(0, requests.size());
    }

    @Test
    void testFindByRequestorIdOneObject() {
        entityManager.persist(requestor);
        entityManager.persist(request);
        List<ItemRequest> requests = itemRequestDao.findByRequestorId(requestor.getId());
        assertNotNull(requests);
        assertEquals(1, requests.size());
    }

    @Test
    void testFindByRequestorIdNotEmpty() {
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size, Sort.Direction.DESC, "created");

        List<ItemRequest> requests = itemRequestDao.findByRequestorIdNot(1L, page).getContent();
        assertNotNull(requests);
        assertEquals(0, requests.size());
    }

    @Test
    void testFindByRequestorIdNotOneObject() {
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size, Sort.Direction.DESC, "created");
        entityManager.persist(requestor);
        entityManager.persist(request);

        List<ItemRequest> requests = itemRequestDao.findByRequestorIdNot(1L, page).getContent();
        assertEquals(1, requests.size());
    }

    @Test
    void testFindByRequestorIdNotTwoObjectSort() {
        int pageNum = 0;
        int size = 1;
        entityManager.persist(requestor);
        PageRequest page = PageRequest.of(pageNum, size, Sort.Direction.DESC, "created");
        ItemRequest request2 = new ItemRequest();
        request2.setDescription("description2");
        request2.setRequestor(requestor);
        entityManager.persist(request2);
        List<ItemRequest> requests = itemRequestDao.findByRequestorIdNot(2L, page).getContent();
        assertEquals(1, requests.size());
        assertEquals("description2", requests.get(0).getDescription());
    }


}