package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(groups = ValidationGroups.Create.class, message = "name can't be BLANK")
    private String name;

    @NotBlank(groups = ValidationGroups.Create.class, message = "description can't be BLANK")
    @Size(max = 255, message = "description length should not be more than 255 characters")
    private String description;

    @NotNull(groups = ValidationGroups.Create.class, message = "available can't be null")
    private Boolean available;

    private Long requestId;
}