package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    private final UserDao userDao;

    private final CommentDao commentDao;

    private final BookingDao bookingDao;

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        log.info("Попытка получения списка предметов по userId");
        List<Item> items =itemDao.findByItemId(userId);
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        log.info("Попытка создания с id {} itemDto {}",userId,itemDto);
        Item item = itemDao.save(ItemMapper.mapToItem(itemDto, userDao.findById(userId)
                .orElseThrow( () -> new NotFoundException("User не найден по id = " + userId))));

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        log.info("Попытка удаления по userId {} и itemId {}",userId,itemId);
        itemDao.deleteByItemIdAndUserId(userId,itemId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        log.info("Попытка обновления с userId {} itemId {} ItemDto {}",userId,itemId,itemDto);
        Item item = itemDao.getWithUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new NotFoundException("Item не был найден"));
        boolean isHasName = itemDto.getName() != null;
        boolean isHasDescription = itemDto.getDescription() != null;
        boolean isHasAvailable = itemDto.getAvailable() != null;

        if(isHasAvailable) item.setAvailable(itemDto.getAvailable());
        if(isHasName) item.setName(itemDto.getName());
        if(isHasDescription) item.setDescription(itemDto.getDescription());
        log.info("Теперь item {}", item);
        return ItemMapper.mapToItemDto(itemDao.save(item));
    }

    @Override
    public ItemWithCommentDto getItem(long itemId) {
        log.info("Попытка получения предмета по itemId {}", itemId);
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден по id = " + itemId));
        List<Booking> bookings = bookingDao.findByItemId(itemId);
        final LocalDateTime now = LocalDateTime.now();
        List<Comment> comments = commentDao.findByItem(item);
        LocalDateTime next = LocalDateTime.now();
        LocalDateTime last = LocalDateTime.now();
        Booking nextB = Booking.builder().build();
        Booking lastB = Booking.builder().build();

        for (Booking booking: bookings){
            //Долго ломал голову и не хотел использовать запросы к sql с сортировкой по дате, сделал всё циклом
            // с кучей объектов
            if (booking.getStart().isAfter(next)){
                if(now.isEqual(next) || booking.getStart().isBefore(next)){
                    next = booking.getStart();
                    nextB = booking;
                }
            }
            if(booking.getEnd().isBefore(last)){
                if(now.isEqual(last) || booking.getStart().isAfter(last)) {
                    last = booking.getEnd();
                    lastB = booking;
                }
            }
            return ItemMapper.mapToItemWitchCommentDto(item,lastB,nextB,comments);
        }



        return null;
    }

    @Override
    public List<ItemDto> getAll() {
        log.info("Получение всего списка");
        return ItemMapper.mapToItemDto(itemDao.findAll());
    }

    @Override
    public List<ItemDto> searchByText(String text, long userId) {
        log.info("Получение списка по тексту text = {}",text);
        return ItemMapper.mapToItemDto(itemDao.getListILikeByText(text, userId));
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemDao.getWithUserIdAndItemId(userId, itemId)
                .orElseThrow( () -> new NotFoundException("Предмет не найден по itemId = " + itemId +
                        " и userId = " + userId));
        User user = item.getOwner();

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .author(user)
                .item(item)
                .build();

        return CommentMapper.mapToCommentDto(commentDao.save(comment));
    }


}
