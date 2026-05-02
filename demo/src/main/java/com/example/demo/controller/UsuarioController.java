package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.tablas.Usuario;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;


    // Endpoint para que un usuario vea su propio perfil
    @GetMapping("/me/{username}")
    public UsuarioDTO findByUsername(@PathVariable String username) {
        Usuario u = usuarioRepository.findByUsername(username);
        if (u == null) return null;
        return new UsuarioDTO(u.getIdUsuario(), u.getNombreUsuario());
}
}