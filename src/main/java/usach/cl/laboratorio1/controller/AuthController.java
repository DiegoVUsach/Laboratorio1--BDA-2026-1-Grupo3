package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.dto.AuthResponse;
import usach.cl.laboratorio1.dto.LoginRequest;
import usach.cl.laboratorio1.repository.UsuarioRepository;
import usach.cl.laboratorio1.service.JwtService;
import usach.cl.laboratorio1.tablas.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// @RestController: combina @Controller + @ResponseBody.
// Significa que cada metodo retorna datos (JSON), no una vista HTML.
// @RequestMapping: todas las rutas de esta clase empiezan con /api/auth
// @CrossOrigin("*"): permite peticiones desde cualquier origen (frontend)
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

    // POST /api/auth/login
    // Ruta PUBLICA (no necesita token).
    // Recibe username + password, valida contra la BD,
    // y devuelve un token JWT si las credenciales son correctas.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req == null || req.getUsername() == null) {
            return ResponseEntity.badRequest().body("Error: No enviaste el JSON en el Body");
        }

        Usuario user = usuarioRepository.findByUsername(req.getUsername());
        if (user != null && passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            String token = jwtService.createToken(user.getNombreUsuario(), user.getRol());
            return ResponseEntity.ok(new AuthResponse(token));
        }

        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    // POST /api/auth/register
    // Ruta PUBLICA. Crea un nuevo usuario en la BD.
    // Si no se especifica rol, se asigna "USER" por defecto.
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            if (usuario.getRol() == null) usuario.setRol("USER");
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }
}