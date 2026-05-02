package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.LiderService;
import com.example.demo.tablas.Lider;

@RestController
@RequestMapping("/api/lider")
@CrossOrigin("*")
public class LiderController {

    @Autowired
    private LiderService liderService;

    @GetMapping
    public List<Lider> listLideres() {
        return liderService.listLideres();
    }

    @GetMapping("/{id}")
    public Lider FindById(@PathVariable Integer id) {
        return liderService.findById(id);
    }
}