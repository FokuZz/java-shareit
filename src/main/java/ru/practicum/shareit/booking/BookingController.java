package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Component
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Later-User-Id") long userId,
                                    @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmation(@RequestHeader("X-Later-User-Id") long userId,
                                   @RequestParam("approved") boolean approved,
                                   @PathVariable("bookingId") Long bookingId) {
        return bookingService.confirmation(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getOnlyOwnerOrBooker(@RequestHeader("X-Later-User-Id") long userId,
                                           @PathVariable("bookingId") Long bookingId) {
        return bookingService.getOnlyOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getListOfUserBooker(@RequestHeader("X-Later-User-Id") long userId,
                                                @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                State state) {
        return bookingService.getListOfUserBooker(userId, state);
    }

    @GetMapping
    public List<BookingDto> getListBookerOfOwnerItems(
            @RequestHeader("X-Later-User-Id") long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL")
            State state) {
        return bookingService.getListBookerOfOwnerItems(userId, state);
    }
}
