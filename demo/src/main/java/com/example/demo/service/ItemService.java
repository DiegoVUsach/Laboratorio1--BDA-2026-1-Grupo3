package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ItemRepository;
import com.example.demo.tablas.Item;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Integer id) {
        return itemRepository.findById(id);
    }

    public void save(Item item) {
        itemRepository.save(item);
    }

    // Punto 3: Procedimiento Almacenado 1
    // Llama a la función de PostgreSQL que entrega el item y resta DKP
    public void distributeLoot(Integer idPersonaje, Integer idItem) {
        itemRepository.distributeLootProcedure(idPersonaje, idItem);
    }
}