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
        Item item;
        if (isReserved) {
            item = ReservedItem.builder()
                    .name(dto.getName())
                    .content(dto.getContent())
                    .price(dto.getPrice())
                    .stockQuantity(dto.getStockQuantity())
                    .reservationStart(dto.getReservationStart())
                    .reservationEnd(dto.getReservationEnd())
                    .build();
        } else {
            item = GeneralItem.builder()
                    .name(dto.getName())
                    .content(dto.getContent())
                    .price(dto.getPrice())
                    .stockQuantity(dto.getStockQuantity())
                    .build();
        }
        Item savedItem = itemRepository.save(item);
        return ItemDetailDto.of(savedItem);
    }

    @Transactional
    public ItemDetailDto updateItem(Long itemId, UpdateItemDto updateItemDto) {
        Item item = findByItemId(itemId);
        item.updateItem(updateItemDto);

        if (item instanceof ReservedItem) {
            ReservedItem reservedItem = (ReservedItem) item;
            if (updateItemDto.getReservationStart() != null && updateItemDto.getReservationEnd() != null) {
                reservedItem.updateReservationTimes(updateItemDto.getReservationStart(), updateItemDto.getReservationEnd());
            }
        }
        return ItemDetailDto.of(itemRepository.save(item));
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
