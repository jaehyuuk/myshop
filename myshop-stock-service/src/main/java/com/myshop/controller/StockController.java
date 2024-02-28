package com.myshop.controller;

import com.myshop.global.dto.CreateStockDto;
import com.myshop.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<CreateStockDto>> getAllStocks() {
        List<CreateStockDto> stocks = stockService.findAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/find/{orderId}")
    public ResponseEntity<?> findStock(@PathVariable Long orderId) {
        return stockService.findStockById(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long orderId) {
        stockService.deleteStock(orderId);
        return ResponseEntity.ok().build();
    }
}
