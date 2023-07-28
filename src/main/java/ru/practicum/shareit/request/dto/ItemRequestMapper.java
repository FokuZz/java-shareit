package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestDto> mapToItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(mapToItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }

    public static ItemRequestItemsDto mapToItemRequestItemsDto(ItemRequest itemRequest) {
        return ItemRequestItemsDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestItemsDto> mapToItemRequestItemsDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestItemsDto> itemDtoRequestItemsDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemDtoRequestItemsDto.add(mapToItemRequestItemsDto(itemRequest));
        }
        return itemDtoRequestItemsDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static ItemRequest toItemRequest(ItemRequestItemsDto itemRequestNewDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestNewDto.getDescription());
        return itemRequest;
    }
}
