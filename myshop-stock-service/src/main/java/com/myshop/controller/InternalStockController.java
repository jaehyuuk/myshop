package com.myshop.controller;

import com.myshop.global.dto.CreateStockDto;
import com.myshop.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/stocks")
@RequiredArgsConstructor
public class InternalStockController {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<?> saveStock(@RequestBody CreateStockDto stock) {
        stockService.saveStock(stock);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long orderId) {
        stockService.deleteStock(orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateStockQuantity(@PathVariable Long itemId, @RequestParam int stockQuantity) {
        stockService.updateStockQuantity(itemId, stockQuantity);
        return ResponseEntity.ok().build();
    }
}
