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
        ScanOptions options = ScanOptions.scanOptions().match("stock:*").count(100).build();

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

    public Optional<CreateStockDto> findStockById(Long stockId) {
        String key = "stock:" + stockId;
        String stockJson = redisTemplate.opsForValue().get(key);
        if (stockJson != null && !stockJson.isEmpty()) {
            try {
                CreateStockDto stock = objectMapper.readValue(stockJson, CreateStockDto.class);
                return Optional.of(stock);
            } catch (Exception e) {
                log.error("Failed to deserialize stock for orderId: {}. Error: {}", stockId, e.getMessage(), e);
            }
        } else {
            log.info("No stock found for orderId: {}", stockId);
        }
        return Optional.empty();
    }

    public void saveStock(CreateStockDto stockDto) {
        String key = "stock:" + stockDto.getStockId();
        try {
            String stockJson = objectMapper.writeValueAsString(stockDto);
            redisTemplate.opsForValue().set(key, stockJson);
            log.info("Successfully saved stock for orderId: {} as JSON: {}", stockDto.getStockId(), stockJson);
        } catch (Exception e) {
            log.error("Failed to serialize stock for orderId: {} to JSON. Error: {}", stockDto.getStockId(), e.getMessage(), e);
        }
    }

    public void deleteStock(Long stockId) {
        String key = "stock:" + stockId;
        redisTemplate.delete(key);
    }

    public void updateStockQuantity(Long itemId, int newStockQuantity) {
        String key = "item:" + itemId;
        redisTemplate.opsForHash().put(key, "stockQuantity", String.valueOf(newStockQuantity));
    }
}
