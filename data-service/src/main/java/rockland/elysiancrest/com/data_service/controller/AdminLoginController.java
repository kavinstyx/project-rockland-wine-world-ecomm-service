package rockland.elysiancrest.com.data_service.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.authFilter.JwtUtil;
import rockland.elysiancrest.com.data_service.data.LoginRequest;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.Status;
import rockland.elysiancrest.com.data_service.entity.admin.Admin;
import rockland.elysiancrest.com.data_service.service.AdminService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/auth/")
public class AdminLoginController {

    private final AdminService adminService;
    private final JwtUtil jwtTokenUtil;

    public AdminLoginController(AdminService adminService, JwtUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Response<Map<String, Object>>> loginAdmin(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the admin
            Admin admin = adminService.authenticateAdmin(loginRequest.getEmail(), loginRequest.getPassword());

            // Generate JWT token
            String jwtToken = jwtTokenUtil.generateToken(admin.getEmail());

            // Save the token in the admin entity
            admin.setToken(jwtToken);
            adminService.saveAdmin(admin);

            // Prepare the response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", jwtToken);
            responseData.put("email", admin.getEmail());

            // Create and return the successful response
            Response<Map<String, Object>> response = Response.<Map<String, Object>>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .data(responseData)
                    .message("Login successful")
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            // Create and return the error response
            Response<Map<String, Object>> response = Response.<Map<String, Object>>builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(Status.ERROR)
                    .message("Authentication failed")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
