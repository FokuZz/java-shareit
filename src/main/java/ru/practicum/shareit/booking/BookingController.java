package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoObjects;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Component
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoObjects createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoObjects confirmation(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam("approved") boolean approved,
                                          @PathVariable("bookingId") Long bookingId) {
        return bookingService.confirmation(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoObjects getOnlyOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("bookingId") Long bookingId) {
        return bookingService.getOnlyOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoObjects> getListOfUserBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(name = "state",
                                                               required = false, defaultValue = "ALL") State state,
                                                       @RequestParam(defaultValue = "0", required = false)
                                                       @Min(value = 0, message = "Нельзя вводить отрицательные число")
                                                       int from,
                                                       @RequestParam(defaultValue = "10", required = false)
                                                       @Positive(message = "Нельзя вводить отрицательные число")
                                                       int size) {
        return bookingService.getListOfUserBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoObjects> getListBookerOfOwnerItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(
                    name = "state", required = false, defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0", required = false)
            @Min(value = 0, message = "Нельзя вводить отрицательные число")
            int from,
            @RequestParam(defaultValue = "10", required = false)
            @Positive(message = "Нельзя вводить отрицательные число")
            int size) {
        return bookingService.getListBookerOfOwnerItems(userId, state, from, size);
    }
}
