package com.myshop.item.controller;

import com.myshop.item.dto.*;
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

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailDto> getItemById(@PathVariable("itemId") Long itemId) {
        ItemDetailDto itemDetailDto = itemService.getItemById(itemId);
        return ResponseEntity.ok(itemDetailDto);
    }

    @GetMapping("/stock/{itemId}")
    public ResponseEntity<ItemStockDto> getItemStock(@PathVariable Long itemId) {
        ItemStockDto itemStockDto = itemService.getItemStock(itemId);
        return ResponseEntity.ok(itemStockDto);
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

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
