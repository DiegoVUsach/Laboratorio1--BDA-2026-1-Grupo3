package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.tablas.Usuario;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Logica a implementar
    public AuthResponse login(LoginRequest request) {
        // Buscar el usuario por nombre
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername());

        // Validar existencia y password
        if (usuario != null && passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            // Generar token incluyendo el rol 
            String token = jwtService.createToken(usuario.getNombreUsuario());
            return new AuthResponse(token);
        }
        
        throw new RuntimeException("Credenciales inválidas");
    }

    // Logica a implementar
    public void register(Usuario usuario) {
        // Cifrar password antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }
}