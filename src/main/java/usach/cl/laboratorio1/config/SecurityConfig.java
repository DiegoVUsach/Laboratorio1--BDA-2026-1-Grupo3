package usach.cl.laboratorio1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration: le dice a Spring que esta clase contiene configuracion.
// @EnableWebSecurity: activa la seguridad HTTP.
// @EnableMethodSecurity: permite usar @PreAuthorize en los controllers.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Lee la clave secreta del application.properties
    @Value("${app.jwt.secret}")
    private String secretKey;

    // PasswordEncoder define como se comparan las passwords.
    // NoOpPasswordEncoder = texto plano (sin encriptacion).
    // En produccion se usaria BCryptPasswordEncoder.
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // Configuracion principal de seguridad:
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {})
                // Desactivar CSRF porque usamos JWT (no cookies)
                .csrf(csrf -> csrf.disable())
                // No usar sesiones: cada peticion se autentica con su token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Reglas de acceso:
                .authorizeHttpRequests(auth -> auth
                        // /api/auth/** es publico (login y registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Todo lo demas requiere un token valido
                        .anyRequest().authenticated()
                )
                // Agregar nuestro JwtFilter ANTES del filtro default de Spring.
                // Asi cuando llega una peticion, primero pasa por JwtFilter
                // (que lee el token) y despues Spring Security decide si dejar pasar.
                .addFilterBefore(new JwtFilter(secretKey),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}