package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.RolesRepository;
import com.example.demo.tablas.Roles;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<Roles> listRoles() {
        return rolesRepository.findAll();
    }

    public Roles findById(Integer id) {
        return rolesRepository.findById(id);
    }

}
