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

    @GetMapping("/find/{stockId}")
    public ResponseEntity<?> findStock(@PathVariable Long stockId) {
        return stockService.findStockById(stockId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{stockId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long stockId) {
        stockService.deleteStock(stockId);
        return ResponseEntity.ok().build();
    }
}
