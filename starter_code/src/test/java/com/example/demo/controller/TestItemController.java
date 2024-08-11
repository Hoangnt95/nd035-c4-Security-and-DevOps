package com.example.demo.controller;

import com.example.demo.TestUtil;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestItemController {

    private ItemController itemController;

    private final ItemRepository itemRepository = mock(com.example.demo.model.persistence.repositories.ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController(itemRepository);
        TestUtil.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        Item item = mockItem(0L, "Item test", BigDecimal.valueOf(1.99), "Item Description");
        Item item1 = mockItem(1L, "Item1 test", BigDecimal.valueOf(9.99), "Item1 Description");
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item1);
        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());
        assertEquals(item, responseItems.get(0));
    }

    @Test
    public void testGetItemById() {
        Item item = mockItem(0L, "Item test", BigDecimal.valueOf(1.99), "Item Description");
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item responseItem = response.getBody();
        assertNotNull(responseItem);
        assertEquals(item.getName(), responseItem.getName());
        assertEquals(item.getDescription(), responseItem.getDescription());
    }

    @Test
    public void testGetItemByName() {
        Item item = mockItem(0L, "Item test", BigDecimal.valueOf(1.99), "Item Description");
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findByName(item.getName())).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(1, responseItems.size());
        assertEquals(item, responseItems.get(0));
    }

    public static Item mockItem(Long id, String name, BigDecimal price, String description) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        return item;
    }
}
