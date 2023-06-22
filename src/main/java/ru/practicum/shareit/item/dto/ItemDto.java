package ru.practicum.shareit.item.dto;


import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemDto {
    long id;

    String name;
    String description;

    Boolean available;

    public ItemDto(long id, @NotBlank String name, @NotBlank String description, @NotNull Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return name.equals(itemDto.name) && description.equals(itemDto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
