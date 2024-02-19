package com.myshop.service;

import com.myshop.dto.StockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StringRedisTemplate redisTemplate;

    public void saveStock(StockDto stock) {
        String key = "item:" + stock.getItemId();
        Map<String, String> stockMap = new HashMap<>();
        stockMap.put("itemId", stock.getItemId().toString());
        stockMap.put("name", stock.getName());
        stockMap.put("stockQuantity", String.valueOf(stock.getStockQuantity()));

        redisTemplate.opsForHash().putAll(key, stockMap);
    }

    public Optional<StockDto> findStockById(Long itemId) {
        String key = "item:" + itemId;
        if (!redisTemplate.hasKey(key)) {
            return Optional.empty();
        }

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries != null && !entries.isEmpty()) {
            StockDto stock = new StockDto();
            stock.setItemId(itemId);
            stock.setName((String) entries.get("name"));
            stock.setStockQuantity(Integer.parseInt((String) entries.get("stockQuantity")));
            return Optional.of(stock);
        }
        return Optional.empty();
    }

    public void updateStockQuantity(Long itemId, int newStockQuantity) {
        String key = "item:" + itemId;
        redisTemplate.opsForHash().put(key, "stockQuantity", String.valueOf(newStockQuantity));
    }

    public void deleteStock(Long itemId) {
        String key = "item:" + itemId;
        redisTemplate.delete(key);
    }
}
