package rockland.elysiancrest.com.data_service.service;

import org.springframework.stereotype.Component;
import rockland.elysiancrest.com.data_service.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenService {
    private final Map<String, String> tokens = new HashMap<>();

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.getUsername());
        return token;
    }

    public boolean validateToken(String token) {
        return tokens.containsKey(token);
    }
}

