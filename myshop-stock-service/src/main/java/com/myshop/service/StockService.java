package com.myshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.global.dto.CreateStockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public List<CreateStockDto> findAllStocks() {
        List<CreateStockDto> stocks = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match("order:*").count(100).build();

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                String stockJson = redisTemplate.opsForValue().get(key);
                if (stockJson != null && !stockJson.isEmpty()) {
                    try {
                        CreateStockDto stock = objectMapper.readValue(stockJson, CreateStockDto.class);
                        stocks.add(stock);
                    } catch (Exception e) {
                        log.error("Failed to deserialize stock JSON for key: {}. Error: {}", key, e.getMessage(), e);
                    }
                } else {
                    log.warn("No stock information found for key: {}", key);
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving all stocks. Error: {}", e.getMessage(), e);
        }
        return stocks;
    }

    public Optional<CreateStockDto> findStockById(Long orderId) {
        String key = "order:" + orderId;
        String stockJson = redisTemplate.opsForValue().get(key);
        if (stockJson != null && !stockJson.isEmpty()) {
            try {
                CreateStockDto stock = objectMapper.readValue(stockJson, CreateStockDto.class);
                return Optional.of(stock);
            } catch (Exception e) {
                log.error("Failed to deserialize stock for orderId: {}. Error: {}", orderId, e.getMessage(), e);
            }
        } else {
            log.info("No stock found for orderId: {}", orderId);
        }
        return Optional.empty();
    }

    public void saveStock(CreateStockDto stock) {
        String key = "order:" + stock.getOrderId();
        try {
            String stockJson = objectMapper.writeValueAsString(stock);
            redisTemplate.opsForValue().set(key, stockJson);
            log.info("Successfully saved stock for orderId: {} as JSON: {}", stock.getOrderId(), stockJson);
        } catch (Exception e) {
            log.error("Failed to serialize stock for orderId: {} to JSON. Error: {}", stock.getOrderId(), e.getMessage(), e);
        }
    }

    public void deleteStock(Long orderId) {
        String key = "order:" + orderId;
        redisTemplate.delete(key);
    }

    public void updateStockQuantity(Long itemId, int newStockQuantity) {
        String key = "item:" + itemId;
        redisTemplate.opsForHash().put(key, "stockQuantity", String.valueOf(newStockQuantity));
    }
}
