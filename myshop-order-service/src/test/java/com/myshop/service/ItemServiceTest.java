package com.myshop.service;

import com.myshop.domain.item.GeneralItem;
import com.myshop.domain.item.Item;
import com.myshop.item.dto.CreateItemDto;
import com.myshop.item.dto.ItemDetailDto;
import com.myshop.item.dto.ItemDto;
import com.myshop.item.repository.ItemRepository;
import com.myshop.item.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @InjectMocks
    private ItemService itemService;

    private CreateItemDto createItemDto;
    private Item generalItem;

    @BeforeEach
    void setUp() {
        createItemDto = new CreateItemDto();

        generalItem = GeneralItem.builder()
                .id(1L)
                .name("Test Item")
                .content("Test Content")
                .price(1000)
                .stockQuantity(10)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("모든 아이템 조회")
    void getAllItemsTest() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(generalItem));
        List<ItemDto> items = itemService.getAllItems();
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("아이템 ID로 조회")
    void getItemByIdTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(generalItem));
        ItemDetailDto itemDetail = itemService.getItemById(1L);
        assertNotNull(itemDetail);
        assertEquals("Test Item", itemDetail.getName());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    @DisplayName("아이템 생성")
    void createItemTest() {
        when(itemRepository.save(any(Item.class))).thenReturn(generalItem);
        when(redisTemplate.opsForHash()).thenReturn(mock(HashOperations.class));
        ItemDetailDto savedItem = itemService.createItem(createItemDto, false);
        assertNotNull(savedItem);
        assertEquals("Test Item", savedItem.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("아이템 삭제")
    void deleteItemTest() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(generalItem));
        doNothing().when(itemRepository).deleteById(itemId);
        itemService.deleteItem(itemId);
        verify(itemRepository).deleteById(itemId);
    }
}
