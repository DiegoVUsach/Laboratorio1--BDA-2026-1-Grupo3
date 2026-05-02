package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.JwtService;
import com.example.demo.tablas.Usuario;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*") 
public class AuthController {

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UsuarioRepository usuarioRepository; 

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) LoginRequest req) {
        System.out.println("DEBUG: Petición recibida en /login");
        
        if (req == null) {
            System.out.println("DEBUG: El objeto LoginRequest es NULL");
            return ResponseEntity.badRequest().body("Error: No enviaste el JSON en el Body");
        }

        System.out.println("DEBUG: Username recibido: " + req.getUsername());
        
        Usuario user = usuarioRepository.findByUsername(req.getUsername());
            if (user != null && passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                // Genera el token solo con el nombre de usuario
                String token = jwtService.createToken(user.getNombreUsuario()); 
                return ResponseEntity.ok(new AuthResponse(token));
            }
        
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}