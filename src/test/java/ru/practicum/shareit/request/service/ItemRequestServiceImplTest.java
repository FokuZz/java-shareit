package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {


    @InjectMocks
    ItemRequestServiceImpl service;
    @Mock
    private ItemRequestDao itemRequestDao;
    @Mock
    private UserDao userDao;
    @Mock
    private ItemDao itemDao;
    private User requestor;
    private User owner;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner.setId(1L);

        requestor = new User();
        requestor.setName("name2");
        requestor.setEmail("e2@mail.ru");
        requestor.setId(2L);

        request = new ItemRequest();
        request.setDescription("description");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
    }

    @Test
    void testCreateAll() {
        when(itemRequestDao.save(any())).thenReturn(request);
        when(userDao.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        ItemRequestDto requestDto = service.create(requestor.getId(),
                ItemRequestDto.builder().description("description").build());
        assertNotNull(requestDto);
        assertEquals(request.getId(), requestDto.getId());
        verify(itemRequestDao, times(1)).save(any());
    }

    @Test
    void testCreateFailUserNotFound() {
        long userNotFoundId = 0L;
        String error = String.format("User с id %d не найден", userNotFoundId);
        when(userDao.findById(userNotFoundId)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.create(userNotFoundId, ItemRequestDto.builder().description("description").build())
        );
        assertEquals(error, exception.getMessage());
        verify(itemRequestDao, times(0)).save(any());
    }

    @Test
    void testGetUserItemsReqStandard() {
        long userId = requestor.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestDao.findByRequestorId(userId)).thenReturn(List.of(request));
        List<ItemRequestItemsDto> requests = service.getUserItemsReq(userId);
        assertNotNull(requests);
        assertEquals(1, requests.size());
        verify(itemRequestDao, times(1)).findByRequestorId(userId);
    }

    @Test
    void testGetItemsEmpty() {
        long userId = requestor.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size, Sort.Direction.DESC, "created");
        when(userDao.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemDao.findByRequestIdIn(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(itemRequestDao.findByRequestorIdNot(userId, page)).thenReturn(Page.empty());
        List<ItemRequestItemsDto> requestDtos = service.getItems(userId, from, size);
        assertNotNull(requestDtos);
        assertEquals(0, requestDtos.size());
    }

    @Test
    void testGetItems() {
        long userId = requestor.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size, Sort.Direction.DESC, "created");
        userId = owner.getId();
        long requestId = request.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(owner));
        when(itemDao.findByRequestIdIn(List.of(requestId))).thenReturn(List.of(item));
        when(itemRequestDao.findByRequestorIdNot(userId, page)).thenReturn(new PageImpl<>(List.of(request)));
        List<ItemRequestItemsDto> requestDtos = service.getItems(userId, from, size);
        assertNotNull(requestDtos);
        assertEquals(1, requestDtos.size());
    }

    @Test
    void testGetItemAll() {
        long userId = requestor.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(requestor));
        long requestId = request.getId();
        when(itemRequestDao.findById(requestId)).thenReturn(Optional.of(request));
        when(itemDao.findByRequestId(requestId)).thenReturn(List.of(item));
        ItemRequestItemsDto requestDto = service.getItem(userId, requestId);
        assertNotNull(requestDto);
        assertEquals(requestId, requestDto.getId());
        assertEquals(1, requestDto.getItems().size());
        assertEquals(item.getId(), requestDto.getItems().get(0).getId());
        InOrder inOrder = inOrder(userDao, itemRequestDao, itemDao);
        inOrder.verify(userDao).findById(userId);
        inOrder.verify(itemRequestDao).findById(requestId);
        inOrder.verify(itemDao).findByRequestId(requestId);
    }
}