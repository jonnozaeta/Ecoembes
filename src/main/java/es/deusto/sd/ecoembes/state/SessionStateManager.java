package es.deusto.sd.ecoembes.state;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStateManager {

    public static class SessionInfo {
        private final String token;
        private final String email;
        private final LocalDateTime expiresAt;

        public SessionInfo(String token, String email, LocalDateTime expiresAt) {
            this.token = token;
            this.email = email;
            this.expiresAt = expiresAt;
        }

        public String getToken() {
            return token;
        }

        public String getEmail() {
            return email;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    // Token -> SessionInfo
    private final Map<String, SessionInfo> activeSessions = new ConcurrentHashMap<>();
    private static final long SESSION_DURATION_HOURS = 24;

    /**
     * Crea y almacena un token de sesión activo para un usuario concreto.
     */
    public SessionInfo createSession(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(SESSION_DURATION_HOURS);
        SessionInfo sessionInfo = new SessionInfo(token, email, expiresAt);
        activeSessions.put(token, sessionInfo);
        return sessionInfo;
    }

    /**
     * Comprueba si un token existe y no ha caducado.
     */
    public boolean isValidToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        SessionInfo session = activeSessions.get(token);
        if (session == null) {
            return false;
        }
        if (session.isExpired()) {
            activeSessions.remove(token);
            return false;
        }
        return true;
    }

    /**
     * Obtiene el correo asociado a un token válido.
     */
    public Optional<String> getEmailFromToken(String token) {
        if (!isValidToken(token)) {
            return Optional.empty();
        }
        return Optional.ofNullable(activeSessions.get(token).getEmail());
    }

    /**
     * Invalida una sesión (cerrar sesión).
     */
    public boolean invalidateSession(String token) {
        if (token == null) {
            return false;
        }
        return activeSessions.remove(token) != null;
    }
}
