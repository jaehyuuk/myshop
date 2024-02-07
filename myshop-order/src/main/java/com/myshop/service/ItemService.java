package com.myshop.service;

import com.myshop.domain.item.Item;
import com.myshop.domain.item.GeneralItem;
import com.myshop.domain.item.ReservedItem;
import com.myshop.dto.*;
import com.myshop.global.exception.BadRequestException;
import com.myshop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemDetailDto createGeneralItem(ItemCreateDto dto) {
        GeneralItem item = GeneralItem.builder()
                .name(dto.getName())
                .content(dto.getContent())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        Item savedItem = itemRepository.save(item);
        return ItemDetailDto.of(savedItem);
    }

    public ItemDetailDto createReservedItem(ItemCreateDto dto) {
        ReservedItem item = ReservedItem.builder()
                .name(dto.getName())
                .content(dto.getContent())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .reservationStart(dto.getReservationStart())
                .reservationEnd(dto.getReservationEnd())
                .build();
        Item savedItem = itemRepository.save(item);
        return ItemDetailDto.of(savedItem);
    }

    public List<ItemDto> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream().map(ItemDto::of).collect(Collectors.toList());
    }

    public ItemDetailDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestException("상품이 존재하지 않습니다.")
        );
        return ItemDetailDto.of(item);
    }

    public ItemDetailDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestException("상품이 존재하지 않습니다.")
        );
        item.updateItem(itemUpdateDto);

        if (item instanceof ReservedItem && itemUpdateDto.getReservationStart() != null && itemUpdateDto.getReservationEnd() != null) {
            ((ReservedItem) item).updateReservationTimes(itemUpdateDto.getReservationStart(), itemUpdateDto.getReservationEnd());
        }

        Item updatedItem = itemRepository.save(item);
        return ItemDetailDto.of(updatedItem);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
