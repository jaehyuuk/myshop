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
                CreateStockDto stock = getStockFromRedis(key);
                if (stock != null) {
                    stocks.add(stock);
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving all stocks. Error: {}", e.getMessage(), e);
        }
        return stocks;
    }

    private CreateStockDto getStockFromRedis(String key) {
        String stockJson = redisTemplate.opsForValue().get(key);
        if (stockJson != null && !stockJson.isEmpty()) {
            try {
                return objectMapper.readValue(stockJson, CreateStockDto.class);
            } catch (Exception e) {
                log.error("Failed to deserialize stock JSON for key: {}. Error: {}", key, e.getMessage(), e);
                return null;
            }
        }
        log.warn("No stock information found for key: {}", key);
        return null;
    }

    public Optional<CreateStockDto> findStockByStockId(Long stockId) {
        String key = "stock:" + stockId;
        CreateStockDto stock = getStockFromRedis(key);
        return Optional.ofNullable(stock);
    }

    public List<CreateStockDto> findStocksByOrderId(Long orderId) {
        return findStocksByIndexKey("orderIndex:" + orderId);
    }

    public List<CreateStockDto> findStocksByItemId(Long itemId) {
        return findStocksByIndexKey("itemIndex:" + itemId);
    }

    private List<CreateStockDto> findStocksByIndexKey(String indexKey) {
        Set<String> stockIds = redisTemplate.opsForSet().members(indexKey);
        List<CreateStockDto> stocks = new ArrayList<>();
        for (String stockId : stockIds) {
            CreateStockDto stock = getStockFromRedis("stock:" + stockId);
            if (stock != null) {
                stocks.add(stock);
            }
        }
        return stocks;
    }

    public void deleteStock(Long stockId) {
        String key = "stock:" + stockId;
        redisTemplate.delete(key);
    }

    public void saveStock(CreateStockDto stockDto) {
        String stockKey = "stock:" + stockDto.getStockId();
        String orderIndexKey = "orderIndex:" + stockDto.getOrderId();
        String itemIndexKey = "itemIndex:" + stockDto.getItemId();

        try {
            String stockJson = objectMapper.writeValueAsString(stockDto);
            redisTemplate.opsForValue().set(stockKey, stockJson);

            redisTemplate.opsForSet().add(orderIndexKey, stockDto.getStockId().toString());
            redisTemplate.opsForSet().add(itemIndexKey, stockDto.getStockId().toString());

            log.info("Successfully saved stock for stockId: {} as JSON: {}", stockDto.getStockId(), stockJson);
        } catch (Exception e) {
            log.error("Failed to serialize stock for stockId: {}. Error: {}", stockDto.getStockId(), e.getMessage(), e);
        }
    }

    public void deleteStocksByOrderId(Long orderId) {
        String orderIndexKey = "orderIndex:" + orderId;
        Set<String> stockIds = redisTemplate.opsForSet().members(orderIndexKey);
        if (stockIds != null && !stockIds.isEmpty()) {
            stockIds.forEach(stockId -> {
                String stockKey = "stock:" + stockId;
                redisTemplate.delete(stockKey);
            });
            redisTemplate.delete(orderIndexKey);
            log.info("Deleted all stocks associated with orderId: {}", orderId);
        } else {
            log.info("No stocks found for orderId: {}, nothing to delete", orderId);
        }
    }

    public void updateStockQuantity(Long itemId, int newStockQuantity) {
        String key = "item:" + itemId;
        redisTemplate.opsForHash().put(key, "stockQuantity", String.valueOf(newStockQuantity));
    }
}
