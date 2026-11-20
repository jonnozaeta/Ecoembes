package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.User;
import es.deusto.sd.ecoembes.dto.AuthTokenDTO;
import es.deusto.sd.ecoembes.dto.CredentialsDTO;
import es.deusto.sd.ecoembes.repository.UserRepository;
import es.deusto.sd.ecoembes.state.SessionStateManager;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SessionStateManager sessionStateManager;

    public AuthService(UserRepository userRepository, SessionStateManager sessionStateManager) {
        this.userRepository = userRepository;
        this.sessionStateManager = sessionStateManager;
    }

    @PostConstruct
    public void seedInitialUsers() {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin@ecoembes.es", "admin123", "ADMIN"));
            userRepository.save(new User("operario@ecoembes.es", "operario123", "OPERATOR"));
        }
    }

    public Optional<AuthTokenDTO> authenticate(CredentialsDTO credentials) {
        if (credentials == null || credentials.getEmail() == null || credentials.getPassword() == null) {
            return Optional.empty();
        }
        Optional<User> userOpt = userRepository.findById(credentials.getEmail());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(credentials.getPassword())) {
            SessionStateManager.SessionInfo session = sessionStateManager.createSession(userOpt.get().getEmail());
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
