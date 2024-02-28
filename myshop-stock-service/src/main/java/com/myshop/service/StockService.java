package com.myshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.dto.StockDto;
import com.myshop.dto.UpdateStockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveStock(StockDto stock) {
        String key = "order:" + stock.getOrderId();
        try {
            String stockJson = objectMapper.writeValueAsString(stock);
            redisTemplate.opsForValue().set(key, stockJson);
            log.info("Successfully saved stock for orderId: {} as JSON: {}", stock.getOrderId(), stockJson);
        } catch (Exception e) {
            log.error("Failed to serialize stock for orderId: {} to JSON. Error: {}", stock.getOrderId(), e.getMessage(), e);
        }
    }

    public Optional<StockDto> findStockById(Long orderId) {
        String key = "order:" + orderId;
        if (!redisTemplate.hasKey(key)) {
            return Optional.empty();
        }

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries != null && !entries.isEmpty()) {
            StockDto stock = new StockDto();
            stock.setOrderId(orderId);
            stock.setUserId(Long.parseLong((String) entries.get("userId")));
            stock.setStockQuantity(Integer.parseInt((String) entries.get("stockQuantity")));
            return Optional.of(stock);
        }
        return Optional.empty();
    }

    public void updateStock(Long orderId, UpdateStockDto stockDto) {
        String key = "order:" + orderId;
        Map<String, String> updates = new HashMap<>();
        if (stockDto.getUserId() != null) updates.put("userId", stockDto.getUserId().toString());
        if (stockDto.getStockQuantity() >= 0) updates.put("stockQuantity", String.valueOf(stockDto.getStockQuantity()));

        if (!updates.isEmpty()) { // 업데이트할 내용이 있을 때만 업데이트 실행
            redisTemplate.opsForHash().putAll(key, updates);
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
