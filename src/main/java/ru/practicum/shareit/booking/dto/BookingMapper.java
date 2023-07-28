package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking mapToBooking(BookingDto bookingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDtoObjects mapToBookingDtoOut(Booking booking) {
        return BookingDtoObjects.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoItem mapToBookingDtoItem(Booking booking) {
        return BookingDtoItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDtoItem> mapToBookingDtoItem(Iterable<Booking> bookings) {
        List<BookingDtoItem> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(mapToBookingDtoItem(booking));
        }
        return bookingDtos;
    }

    public static List<BookingDtoObjects> mapToBookingDtoOut(Iterable<Booking> bookings) {
        List<BookingDtoObjects> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(mapToBookingDtoOut(booking));
        }
        return bookingDtos;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> mapToBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(mapToBookingDto(booking));
        }
        return bookingDtos;
    }
}
