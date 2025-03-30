package rockland.elysiancrest.com.data_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rockland.elysiancrest.com.data_service.service.TokenService;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    private final TokenService tokenService;

    public SecureController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/protected-resource")
    public ResponseEntity<String> getProtectedResource(@RequestHeader("Authorization") String token) {
        if (tokenService.validateToken(token)) {
            return ResponseEntity.ok("You have access to this protected resource");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}

