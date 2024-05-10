package project.hollo.JwtToken.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.hollo.JwtToken.Request_Response.AuthenticationResponse;
import project.hollo.JwtToken.Request_Response.RegisterResponse;
import project.hollo.User.RegisterRequest;

import java.io.IOException;

@RestController
@RequestMapping("/hollo/project_numble")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authservice;

    @PostMapping("/register/admin")
    public ResponseEntity<RegisterResponse> register_admin(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authservice.register_admin(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authservice.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authservice.authenticate(request));
    }

    @PostMapping("/refreshToken")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authservice.refreshToken(request, response);
    }

    @DeleteMapping("/delete_user")
    public void delete_user(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        authservice.deleteUser(request, response);
    }
}
