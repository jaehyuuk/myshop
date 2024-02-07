package com.myshop.item.controller;

import com.myshop.item.dto.ItemCreateDto;
import com.myshop.item.dto.ItemDetailDto;
import com.myshop.item.dto.ItemDto;
import com.myshop.item.dto.ItemUpdateDto;
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

    @PostMapping
    public ResponseEntity<ItemDetailDto> createItem(@RequestBody ItemCreateDto itemCreateDto) {
        ItemDetailDto itemDetailDto;
        if ("RESERVED".equals(itemCreateDto.getType())) {
            itemDetailDto = itemService.createReservedItem(itemCreateDto);
        } else {
            itemDetailDto = itemService.createGeneralItem(itemCreateDto);
        }
        return new ResponseEntity<>(itemDetailDto, HttpStatus.CREATED);
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<ItemDetailDto> updateItem(@PathVariable Long itemId, @RequestBody ItemUpdateDto itemUpdateDto) {
        ItemDetailDto updatedItemDetailDto = itemService.updateItem(itemId, itemUpdateDto);
        return ResponseEntity.ok(updatedItemDetailDto);
    }

    @DeleteMapping("/{item_id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("item_id") Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
