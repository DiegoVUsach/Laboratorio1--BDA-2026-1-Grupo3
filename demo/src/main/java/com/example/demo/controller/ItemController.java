package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ItemService;
import com.example.demo.tablas.Item;

@RestController
@RequestMapping("/api/items")
@CrossOrigin("*")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Item item) {
        itemService.save(item);
        return ResponseEntity.ok("Item creado correctamente");
    }

    // Endpoint para distribuir botín
    @PostMapping("/distribuir")
    public ResponseEntity<?> distribute(@RequestParam Integer idPersonaje, @RequestParam Integer idItem) {
        try {
            itemService.distributeLoot(idPersonaje, idItem);
            return ResponseEntity.ok("Loot distribuido y DKP deducido");
        } catch (Exception e) {
            // Si el procedimiento lanza un EXCEPTION (ej. DKP insuficientes), llegará aquí
            return ResponseEntity.badRequest().body("Error distribuyendo loot: " + e.getMessage());
        }
    }
}