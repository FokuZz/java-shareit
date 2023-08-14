package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    private final UserDao userDao;

    private final CommentDao commentDao;

    private final BookingDao bookingDao;

    @Override
    public List<ItemWithCommentDto> getItemsByUserId(long userId, int from, int size) {
        log.info("Попытка получения списка предметов по userId = {}", userId);
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> items = itemDao.findItemsByOwnerId(userId, page).getContent();
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, ItemWithCommentDto> itemsWithIds = new HashMap<>();
        items.forEach(item -> itemsWithIds.put(item.getId(), ItemMapper.mapToItemWitchCommentDto(item)));
        addCommentsToItems(itemsWithIds);
        addBookingDatesToItems(itemsWithIds);
        return new ArrayList<>(itemsWithIds.values());
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        log.info("Попытка создания с id {} itemDto {}", userId, itemDto);
        boolean isNullAvailable = itemDto.getAvailable() == null;
        boolean isNullName = itemDto.getName() == null || itemDto.getName().isBlank();
        boolean isNullDescription = itemDto.getDescription() == null;

        if (isNullAvailable) throw new ValidationException("ERROR: Available is null");
        if (isNullName) throw new ValidationException("ERROR: Name is null");
        if (isNullDescription) throw new ValidationException("ERROR: Description is null");

        Item item = itemDao.save(ItemMapper.mapToItem(itemDto,
                userDao.findById(userId).orElseThrow(() -> new NotFoundException("User не найден по id = " + userId))));
        log.info("Получилось Item = " + item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        log.info("Попытка удаления по userId {} и itemId {}", userId, itemId);
        itemDao.deleteByIdAndOwnerId(userId, itemId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        log.info("Попытка обновления с userId {} itemId {} ItemDto {}", userId, itemId, itemDto);
        Item item = itemDao.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Item не был найден"));
        boolean isHasName = itemDto.getName() != null;
        boolean isHasDescription = itemDto.getDescription() != null;
        boolean isHasAvailable = itemDto.getAvailable() != null;

        if (isHasAvailable) item.setAvailable(itemDto.getAvailable());
        if (isHasName) item.setName(itemDto.getName());
        if (isHasDescription) item.setDescription(itemDto.getDescription());
        item = itemDao.save(item);
        log.info("Теперь item {}", item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemWithCommentDto getItem(long userId, long itemId) {
        log.info("Попытка получения предмета по itemId {}", itemId);
        Item item = itemDao.findByIdFetch(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден по id = " + itemId));
        ItemWithCommentDto itemWithCommentDto = ItemMapper.mapToItemWitchCommentDto(item);

        long ownerId = item.getOwner().getId();
        if (ownerId == userId) {
            addBookingsToItem(itemWithCommentDto);
        }
        addCommentsToItem(itemWithCommentDto);
        log.info("Получил класс ItemWithCommentDto = {} ", itemWithCommentDto);
        return itemWithCommentDto;
    }

    @Override
    public List<ItemDto> getAll() {
        log.info("Получение всего списка");
        List<Item> items = itemDao.findAll();
        log.info("Обьектов в списке = {} ", items.size());
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public List<ItemDto> searchByText(String text, int from, int size) {
        log.info("Получение списка по тексту text = {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from / size, size);
        List<Item> items = itemDao.getListILikeByText(text.toLowerCase(), page).getContent();
        log.info("Обьектов в списке = {} ", items.size());
        return ItemMapper.mapToItemDto(items);
    }

    @Override

    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User owner = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User не найден по id = " + userId));
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден по id = " + itemId));

        List<Booking> bookingsItemByUser = bookingDao
                .findByBookerIdAndItemIdAndStatusAndStartIsBefore(userId, itemId, Status.APPROVED, LocalDateTime.now());
        if (bookingsItemByUser.isEmpty()) {
            throw new ValidationException(
                    String.format("User с id %s не арендовал вещь с id %s", userId, itemId));
        }
        if (commentDto.getText().isBlank()) throw new IllegalArgumentException("Text не может быть пустым");
        Comment comment = CommentMapper.mapToComment(commentDto, owner, item);

        return CommentMapper.mapToCommentDto(commentDao.save(comment));
    }

    private void addBookingDatesToItems(Map<Long, ItemWithCommentDto> itemsWithId) {
        Map<Long, List<Booking>> bookings = new HashMap<>();
        List<Long> itemIds = new ArrayList<>(itemsWithId.keySet());

        List<Booking> bookingsList = bookingDao.findByItemIdInAndStatusOrStatusOrderByStartAsc(itemIds,
                Status.APPROVED, Status.WAITING);

        bookingsList.forEach(booking -> bookings.computeIfAbsent(booking.getItem().getId(),
                key -> new ArrayList<>()).add(booking));

        bookings.forEach((key, value) -> lastNextBooking(value, itemsWithId.get(key)));
    }

    private void addBookingsToItem(ItemWithCommentDto itemDto) {
        List<Booking> bookings = bookingDao.findByItemIdAndStatusOrStatusOrderByStartAsc(itemDto.getId(),
                Status.APPROVED, Status.WAITING);
        if (bookings.isEmpty()) {
            return;
        }
        lastNextBooking(bookings, itemDto);
    }

    private void addCommentsToItem(ItemWithCommentDto itemDto) {
        commentDao.findAllByItemIdOrderByCreatedDesc(itemDto.getId())
                .forEach(comment -> itemDto.addComment(CommentMapper.mapToCommentDto(comment)));
    }

    private void addCommentsToItems(Map<Long, ItemWithCommentDto> itemsWithId) {
        List<Long> itemIds = new ArrayList<>(itemsWithId.keySet());
        commentDao.findAllByItemIdInOrderByCreatedDesc(itemIds)
                .forEach(comment -> itemsWithId.get(comment.getItem().getId())
                        .addComment(CommentMapper.mapToCommentDto(comment)));

    }

    private void lastNextBooking(List<Booking> bookings, ItemWithCommentDto itemDto) {
        Booking lastBooking;
        Booking nextBooking = null;
        LocalDateTime now = LocalDateTime.now();
        if (bookings.get(0).getStart().isAfter(now)) {
            itemDto.setNextBooking(BookingMapper.mapToBookingDtoItem(bookings.get(0)));
            return;
        } else {
            lastBooking = bookings.get(0);
        }
        for (int i = 1; i < bookings.size(); i++) {
            if (bookings.get(i).getStart().isAfter(now)) {
                lastBooking = bookings.get(i - 1);
                nextBooking = bookings.get(i);
                break;
            }
        }
        itemDto.setLastBooking(BookingMapper.mapToBookingDtoItem(lastBooking));
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.mapToBookingDtoItem(nextBooking));
        }
    }


}
