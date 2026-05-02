package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.RolesService;
import com.example.demo.tablas.Roles;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin("*")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public List<Roles> listRoles() {
        return rolesService.listRoles();
    }

    @GetMapping("/{id}")
    public Roles findById(@PathVariable Integer id) {
        return rolesService.findById(id);
    }
}