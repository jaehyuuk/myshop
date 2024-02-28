package com.myshop.controller;

import com.myshop.dto.StockDto;
import com.myshop.dto.UpdateStockDto;
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

    @GetMapping("/find/{orderId}")
    public ResponseEntity<?> findStock(@PathVariable Long orderId) {
        return stockService.findStockById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateStock(@PathVariable Long orderId, @RequestBody UpdateStockDto stockDto) {
        stockService.updateStock(orderId, stockDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long orderId) {
        stockService.deleteStock(orderId);
        return ResponseEntity.ok().build();
    }
}
