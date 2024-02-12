package com.myshop.item.service;

import com.myshop.item.domain.Item;
import com.myshop.item.domain.GeneralItem;
import com.myshop.item.domain.ReservedItem;
import com.myshop.global.exception.BadRequestException;
import com.myshop.item.dto.CreateItemDto;
import com.myshop.item.dto.ItemDetailDto;
import com.myshop.item.dto.ItemDto;
import com.myshop.item.dto.UpdateItemDto;
import com.myshop.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream().map(ItemDto::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemDetailDto getItemById(Long itemId) {
        Item item = findByItemId(itemId);
        return ItemDetailDto.of(item);
    }

    @Transactional(readOnly = true)
    public int getItemStockQuantity(Long itemId) {
        Item item = findByItemId(itemId);
        return item.getStockQuantity();
    }

    @Transactional
    public ItemDetailDto createItem(CreateItemDto dto, boolean isReserved) {
        Item item = isReserved ? createReservedItem(dto) : createGeneralItem(dto);
        Item savedItem = itemRepository.save(item);
        return ItemDetailDto.of(savedItem);
    }

    private ReservedItem createReservedItem(CreateItemDto dto) {
        return ReservedItem.builder()
                .name(dto.getName())
                .content(dto.getContent())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .reservationStart(dto.getReservationStart())
                .reservationEnd(dto.getReservationEnd())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    private GeneralItem createGeneralItem(CreateItemDto dto) {
        return GeneralItem.builder()
                .name(dto.getName())
                .content(dto.getContent())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public ItemDetailDto updateItem(Long itemId, UpdateItemDto updateItemDto) {
        Item item = findByItemId(itemId);
        item.updateItem(updateItemDto);
        Item savedItem = itemRepository.save(item);
        return ItemDetailDto.of(savedItem);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    private Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestException("상품이 존재하지 않습니다.")
        );
    }
}
