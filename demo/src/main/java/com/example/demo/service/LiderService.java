package com.example.demo.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.LiderRepository;
import com.example.demo.tablas.Lider;

@Service
public class LiderService {

    @Autowired
    private LiderRepository liderRepository;

    public List<Lider> listLideres() {
        return liderRepository.findAll();
    }

    public Lider findById(Integer id) {
        return liderRepository.findById(id);
    }

}
