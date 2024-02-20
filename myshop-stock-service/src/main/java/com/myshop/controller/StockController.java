package com.myshop.controller;

import com.myshop.dto.StockDto;
import com.myshop.dto.StockUpdateRequest;
import com.myshop.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<?> saveStock(@RequestBody StockDto stock) {
        stockService.saveStock(stock);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> findStock(@PathVariable Long itemId) {
        return stockService.findStockById(itemId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateStockQuantity(@PathVariable Long itemId, @RequestBody StockUpdateRequest request) {
        stockService.updateStockQuantity(itemId, request.getStockQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long itemId) {
        stockService.deleteStock(itemId);
        return ResponseEntity.ok().build();
    }
}
