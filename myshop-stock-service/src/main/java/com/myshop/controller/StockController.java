package com.myshop.controller;

import com.myshop.global.dto.CreateStockDto;
import com.myshop.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/all")
    public ResponseEntity<List<CreateStockDto>> getAllStocks() {
        List<CreateStockDto> stocks = stockService.findAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<CreateStockDto> getStockByStockId(@PathVariable Long stockId) {
        Optional<CreateStockDto> stock = stockService.findStockByStockId(stockId);
        return stock.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<CreateStockDto>> getStocksByOrderId(@PathVariable Long orderId) {
        List<CreateStockDto> stocks = stockService.findStocksByOrderId(orderId);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<CreateStockDto>> getStocksByItemId(@PathVariable Long itemId) {
        List<CreateStockDto> stocks = stockService.findStocksByItemId(itemId);
        return ResponseEntity.ok(stocks);
    }

    @DeleteMapping("/delete/{stockId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long stockId) {
        stockService.deleteStock(stockId);
        return ResponseEntity.ok().build();
    }
}
