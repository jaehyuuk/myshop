package com.myshop.controller;

import com.myshop.dto.StockDto;
import com.myshop.global.dto.StockUpdateRequest;
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
        try {
            stockService.saveStock(stock);
            return ResponseEntity.ok().body("Stock saved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving stock: " + e.getMessage());
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> findStock(@PathVariable Long itemId) {
        try {
            return stockService.findStockById(itemId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error finding stock: " + e.getMessage());
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateStockQuantity(@PathVariable Long itemId, @RequestBody StockUpdateRequest request) {
        try {
            stockService.updateStockQuantity(itemId, request.getStockQuantity());
            return ResponseEntity.ok().body("Stock quantity updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating stock quantity: " + e.getMessage());
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long itemId) {
        stockService.deleteStock(itemId);
        return ResponseEntity.ok().body("Stock deleted successfully");
    }
}
