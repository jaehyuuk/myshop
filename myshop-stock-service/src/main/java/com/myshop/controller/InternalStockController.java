package com.myshop.controller;

import com.myshop.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/stocks")
@RequiredArgsConstructor
public class InternalStockController {
    private final StockService stockService;

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateStockQuantity(@PathVariable Long itemId, @RequestParam int stockQuantity) {
        stockService.updateStockQuantity(itemId, stockQuantity);
        return ResponseEntity.ok().build();
    }
}
