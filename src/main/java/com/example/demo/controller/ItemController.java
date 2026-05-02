package com.example.demo.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.ItemRepository;
import com.example.demo.tablas.Item;

import lombok.Data;

@RestController
@RequestMapping("/api/items")
@CrossOrigin("*")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping
    public List<Item> listarItems() {
        return itemRepository.findAll();
    }

    // Endpoint para que el GM asigne un item a un jugador
    @PostMapping("/repartir")
    public ResponseEntity<?> repartir(@RequestBody BotinRequest request) {
        try {
            itemRepository.repartirBotin(
                request.getIdPersonaje(), 
                request.getIdItem(), 
                request.getIdRaid()
            );
            return ResponseEntity.ok("Botín asignado y DKP descontados.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

@Data
class BotinRequest {
    private Integer idPersonaje;
    private Integer idItem;
    private Integer idRaid;
}