package es.deusto.sd.ecoembes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Token de autenticación emitido tras iniciar sesión")
public class AuthTokenDTO {

    @Schema(description = "Token generado de sesión", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String token;

    @Schema(description = "Email del usuario autenticado", example = "admin@ecoembes.es")
    private String email;

    @Schema(description = "Fecha de expiración del token")
    private LocalDateTime expiresAt;

    public AuthTokenDTO() {
    }

    public AuthTokenDTO(String token, String email, LocalDateTime expiresAt) {
        this.token = token;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
