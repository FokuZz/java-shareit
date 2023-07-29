package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemDao itemDao;

    @Mock
    UserDao userDao;

    @Mock
    BookingDao bookingDao;

    @Mock
    CommentDao commentDao;

    @InjectMocks
    ItemServiceImpl service;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;


    @BeforeEach
    void setup() {
        LocalDateTime start = LocalDateTime.now().minusSeconds(120);
        LocalDateTime end = LocalDateTime.now().minusSeconds(60);
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner.setId(1L);

        booker = new User();
        booker.setName("name2");
        booker.setEmail("e2@mail.ru");
        booker.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Хочешь, задрелю соседей, что мешают спать?");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        comment = new Comment();
        comment.setText("text");
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setId(1L);
    }

    @Test
    void testGetItemsByUserIdEmpty() {
        long userId = booker.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size);
        when(itemDao.findItemsByOwnerId(userId, page)).thenReturn(Page.empty());
        List<ItemWithCommentDto> itemDtos = service.getItemsByUserId(userId, from, size);
        assertNotNull(itemDtos);
        assertEquals(0, itemDtos.size());
    }

    @Test
    void testGetItemsByUserIdOneObject() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        PageRequest page = PageRequest.of(from / size, size);
        when(commentDao.findAllByItemIdInOrderByCreatedDesc(List.of(item.getId())))
                .thenReturn(List.of(comment));
        when(bookingDao.findByItemIdInAndStatusOrStatusOrderByStartAsc(List.of(item.getId()),
                Status.APPROVED, Status.WAITING)).thenReturn(List.of(booking));
        when(itemDao.findItemsByOwnerId(userId, page)).thenReturn(new PageImpl<>(List.of(item)));
        List<ItemWithCommentDto> itemDtos = service.getItemsByUserId(userId, from, size);
        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        assertEquals(booking.getId(), itemDtos.get(0).getLastBooking().getId());
    }

    @Test
    void testCreate() {
        long userId = owner.getId();
        long itemId = item.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(owner));
        when(itemDao.save(any())).thenReturn(item);

        ItemDto itemDtoToSave = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
        ItemDto itemDto = service.create(userId, itemDtoToSave);
        assertNotNull(itemDto);
        assertEquals(itemId, itemDto.getId());
        verify(itemDao, times(1)).save(any());
    }

    @Test
    void testCreateFailFind() {
        long userId = owner.getId();
        ItemDto itemDtoToSave = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.create(userId, itemDtoToSave));
        assertEquals("User не найден по id = 1", exception.getMessage());

    }

    @Test
    void testDeleteStandard() {
        long userId = owner.getId();
        long itemId = item.getId();
        doNothing().when(itemDao).deleteByIdAndOwnerId(itemId, userId);
        service.deleteByUserIdAndItemId(userId, itemId);

        verify(itemDao, times(1)).deleteByIdAndOwnerId(any(), any());
    }

    @Test
    void testUpdateItemStandard() {
        long userId = owner.getId();
        long itemId = item.getId();
        ItemDto itemDtoTest = ItemMapper.mapToItemDto(item);
        when(itemDao.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.of(item));
        when(itemDao.save(item)).thenReturn(item);
        ItemDto itemDto = service.updateItem(userId, itemId, itemDtoTest);
        assertNotNull(itemDto);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable());

    }

    @Test
    void testUpdateItemFailNotFound() {
        long userId = owner.getId();
        long itemId = item.getId();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.updateItem(userId, itemId, ItemDto.builder().description("").build())
        );
        assertEquals("Item не был найден", exception.getMessage());
    }


    @Test
    void testGetItem() {
        long ownerId = owner.getId();
        long itemId = item.getId();
        when(itemDao.findByIdFetch(itemId)).thenReturn(Optional.of(item));
        when(bookingDao.findByItemIdAndStatusOrStatusOrderByStartAsc(itemId,
                Status.APPROVED, Status.WAITING)).thenReturn(List.of(booking));
        when(commentDao.findAllByItemIdOrderByCreatedDesc(itemId))
                .thenReturn(List.of(comment));
        ItemWithCommentDto itemDto = service.getItem(ownerId, itemId);
        assertNotNull(itemDto);
        assertEquals(itemId, itemDto.getId());
        assertEquals(comment.getId(), itemDto.getComments().get(0).getId());
    }

    @Test
    void testGetAll() {
        testCreate();
        List<ItemDto> items = service.getAll();
        assertNotNull(items);
    }

    @Test
    void testSearchByTextEmpty() {
        int from = 0;
        int size = 1;
        String text = "";
        List<ItemDto> itemDtos = service.searchByText(text, from, size);
        assertNotNull(itemDtos);
        assertEquals(0, itemDtos.size());
    }

    @Test
    void testSearchByText() {
        int from = 0;
        int size = 1;
        String text = "дРелЬ";
        when(itemDao.getListILikeByText(any(), any())).thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDto> itemDtos = service.searchByText(text, from, size);
        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        assertEquals(item.getId(), itemDtos.get(0).getId());
    }

    @Test
    void testCreateCommentStandard() {
        long userId = booker.getId();
        long itemId = item.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(owner));
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingDao
                .findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(commentDao.save(any())).thenReturn(comment);
        CommentDto commentDto = service.createComment(userId, itemId, CommentDto.builder().text("text").build());
        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());

        verify(commentDao, times(1)).save(any());
    }

    @Test
    void testCreateCommentFailBlankText() {
        long userId = booker.getId();
        long itemId = item.getId();
        when(userDao.findById(userId)).thenReturn(Optional.of(owner));
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingDao
                .findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createComment(userId, itemId, CommentDto.builder().text("").build())
        );
    }

    @Test
    void testCreateCommentFailItemNotFound() {
        long itemId = item.getId();
        long ownerId = owner.getId();
        when(userDao.findById(ownerId)).thenReturn(Optional.of(owner));
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.createComment(ownerId, itemId, CommentDto.builder().text("text").build())
        );
        assertEquals("Item не найден по id = 1", exception.getMessage());
    }

    @Test
    void testCreateCommentFailUserNotFound() {
        long itemId = item.getId();
        long ownerId = owner.getId();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.createComment(ownerId, itemId, CommentDto.builder().text("text").build())
        );
        assertEquals("User не найден по id = 1", exception.getMessage());
    }

    @Test
    void testCreateCommentFailUserRandom() {
        long itemId = item.getId();
        long ownerId = 5;
        when(itemDao.findById(itemId)).thenReturn(Optional.of(item));
        when(userDao.findById(ownerId)).thenReturn(Optional.of(owner));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createComment(ownerId, itemId, CommentDto.builder().text("text").build())
        );
        assertEquals("User с id 5 не арендовал вещь с id 1", exception.getMessage());
    }
}