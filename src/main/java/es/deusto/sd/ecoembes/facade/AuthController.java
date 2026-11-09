package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.dto.AuthTokenDTO;
import es.deusto.sd.ecoembes.dto.CredentialsDTO;
import es.deusto.sd.ecoembes.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación (Facade)", description = "Controlador Facade para login y logout de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "login", description = "Autentica al usuario en el sistema y genera un token de sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación correcta, token devuelto"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<AuthTokenDTO> login(@Valid @RequestBody CredentialsDTO credentials) {
        Optional<AuthTokenDTO> token = authService.authenticate(credentials);
        return token.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/logout")
    @Operation(summary = "logout", description = "Cierra la sesión invalidando el token activo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sesión cerrada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token no válido o sesión ya cerrada")
    })
    public ResponseEntity<Void> logout(
            @Parameter(description = "Token de autenticación", required = true)
            @RequestParam("token") String token) {

        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean success = authService.logout(token);
        if (success) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
