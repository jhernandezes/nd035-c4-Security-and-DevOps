package com.example.demo.controller;


import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void getItems_success() throws Exception {

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");
        item1.setPrice(BigDecimal.valueOf(10.0));
        item1.setDescription("Sample description");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");
        item2.setPrice(BigDecimal.valueOf(20.0));
        item2.setDescription("Sample description");


        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemRepository.findAll()).thenReturn(items);

        mockMvc.perform(get("/api/item")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Item1")))
                .andExpect(jsonPath("$[1].name", is("Item2")));
    }

    @Test
    void getItemById_found() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setPrice(BigDecimal.valueOf(10.0));
        item.setDescription("Sample description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/item/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Item1")));
    }

    @Test
    void getItemById_notFound() throws Exception {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/item/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemsByName_found() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setPrice(BigDecimal.valueOf(10.0));
        item.setDescription("Sample description");

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findByName("Item1")).thenReturn(items);

        mockMvc.perform(get("/api/item/name/Item1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Item1")));
    }

    @Test
    void getItemsByName_notFound() throws Exception {
        when(itemRepository.findByName("UnknownItem")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/item/name/UnknownItem")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

