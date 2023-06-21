package ru.practicum.shareit.item.dto;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemDto {
    String name;

    String description;

    boolean available;

    Long requestId;

    public ItemDto(String name, String description, boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
