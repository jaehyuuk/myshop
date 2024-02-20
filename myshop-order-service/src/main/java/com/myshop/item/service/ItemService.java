package com.myshop.item.service;

import com.myshop.domain.item.Item;
import com.myshop.domain.item.GeneralItem;
import com.myshop.domain.item.ReservedItem;
import com.myshop.global.exception.BadRequestException;
import com.myshop.item.dto.*;
import com.myshop.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final StringRedisTemplate redisTemplate;

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
    public ItemStockDto getItemStock(Long itemId) {
        Item item = findByItemId(itemId);
        return ItemStockDto.of(item);
    }

    @Transactional
    public ItemDetailDto createItem(CreateItemDto dto, boolean isReserved) {
        Item item = isReserved ? createReservedItem(dto) : createGeneralItem(dto);
        Item savedItem = itemRepository.save(item);
        saveItemToRedis(savedItem); // redis 저장
        return ItemDetailDto.of(savedItem);
    }

    @Transactional
    public ItemDetailDto updateItem(Long itemId, UpdateItemDto updateItemDto) {
        Item item = findByItemId(itemId);
        item.updateItem(updateItemDto);
        Item savedItem = itemRepository.save(item);
        saveItemToRedis(savedItem); // redis 저장
        return ItemDetailDto.of(savedItem);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        findByItemId(itemId);
        deleteItemToRedis(itemId);
        itemRepository.deleteById(itemId);
    }

    private Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestException("상품이 존재하지 않습니다.")
        );
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

    private void saveItemToRedis(Item item) {
        String key = "item:" + item.getId();
        redisTemplate.opsForHash().put(key, "name", item.getName());
        redisTemplate.opsForHash().put(key, "stockQuantity", String.valueOf(item.getStockQuantity()));

        if (item instanceof ReservedItem) {
            ReservedItem reservedItem = (ReservedItem) item;
            LocalDateTime reservationStart = reservedItem.getReservationStart();
            LocalDateTime reservationEnd = reservedItem.getReservationEnd();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            redisTemplate.opsForHash().put(key, "reservationStart", reservationStart.format(formatter));
            redisTemplate.opsForHash().put(key, "reservationEnd", reservationEnd.format(formatter));
        }
    }

    private void deleteItemToRedis(Long itemId) {
        String key = "item:" + itemId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }
    }
}
