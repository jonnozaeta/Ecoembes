package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.User;
import es.deusto.sd.ecoembes.dto.AuthTokenDTO;
import es.deusto.sd.ecoembes.dto.CredentialsDTO;
import es.deusto.sd.ecoembes.state.SessionStateManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final SessionStateManager sessionStateManager;
    // Usuarios registrados simulados en memoria
    private final Map<String, User> registeredUsers = new ConcurrentHashMap<>();

    public AuthService(SessionStateManager sessionStateManager) {
        this.sessionStateManager = sessionStateManager;
        // Usuarios de demostración
        registeredUsers.put("admin@ecoembes.es", new User("admin@ecoembes.es", "admin123", "ADMIN"));
        registeredUsers.put("operario@ecoembes.es", new User("operario@ecoembes.es", "operario123", "OPERATOR"));
    }

    public Optional<AuthTokenDTO> authenticate(CredentialsDTO credentials) {
        if (credentials == null || credentials.getEmail() == null || credentials.getPassword() == null) {
            return Optional.empty();
        }
        User user = registeredUsers.get(credentials.getEmail());
        if (user != null && user.getPassword().equals(credentials.getPassword())) {
            SessionStateManager.SessionInfo session = sessionStateManager.createSession(user.getEmail());
            return Optional.of(new AuthTokenDTO(session.getToken(), session.getEmail(), session.getExpiresAt()));
        }
        return Optional.empty();
    }

    public boolean logout(String token) {
        return sessionStateManager.invalidateSession(token);
    }

    public boolean validateToken(String token) {
        return sessionStateManager.isValidToken(token);
    }
}
