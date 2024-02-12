package com.myshop.item.controller;

import com.myshop.item.dto.CreateItemDto;
import com.myshop.item.dto.ItemDetailDto;
import com.myshop.item.dto.ItemDto;
import com.myshop.item.dto.UpdateItemDto;
import com.myshop.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{item_id}")
    public ResponseEntity<ItemDetailDto> getItemById(@PathVariable("item_id") Long itemId) {
        ItemDetailDto itemDetailDto = itemService.getItemById(itemId);
        return ResponseEntity.ok(itemDetailDto);
    }

    @GetMapping("/stock/{itemId}")
    public ResponseEntity<Integer> getItemStockQuantity(@PathVariable Long itemId) {
        int stockQuantity = itemService.getItemStockQuantity(itemId);
        return ResponseEntity.ok(stockQuantity);
    }

    @PostMapping
    public ResponseEntity<ItemDetailDto> createItem(@RequestBody CreateItemDto createItemDto) {
        boolean isReserved = "RESERVED".equals(createItemDto.getType());
        ItemDetailDto itemDetailDto = itemService.createItem(createItemDto, isReserved);
        return new ResponseEntity<>(itemDetailDto, HttpStatus.CREATED);
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<ItemDetailDto> updateItem(@PathVariable Long itemId, @RequestBody UpdateItemDto updateItemDto) {
        ItemDetailDto updatedItemDetailDto = itemService.updateItem(itemId, updateItemDto);
        return ResponseEntity.ok(updatedItemDetailDto);
    }

    @DeleteMapping("/{item_id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("item_id") Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
