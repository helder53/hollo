package project.hollo.JwtToken.Auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.hollo.JwtToken.Jwt.JwtService;
import project.hollo.JwtToken.Request_Response.AuthenticationResponse;
import project.hollo.JwtToken.Request_Response.RegisterResponse;
import project.hollo.JwtToken.Token.Token;
import project.hollo.JwtToken.Token.TokenRepository;
import project.hollo.JwtToken.Token.TokenType;
import project.hollo.User.RegisterRequest;
import project.hollo.User.Role;
import project.hollo.User.User;
import project.hollo.User.UserRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    public final UserRepository userRepository;
    public final TokenRepository tokenRepository;
    public final JwtService jwtService;
    public final PasswordEncoder passwordEncoder;
    public final AuthenticationManager authenticationManager;

    public RegisterResponse register_admin(RegisterRequest request) {
        var user = User.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthDay(request.getBirthDay())
                .role(Role.valueOf("ADMIN"))
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        saveUserToken(savedUser, jwtToken);
        return RegisterResponse.builder()
                .id(user.getNickname())
                .name(user.getName())
                .build();
    }

    public RegisterResponse register(RegisterRequest request) {
        var user = User.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthDay(request.getBirthDay())
                .role(Role.valueOf("MANAGER"))
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        saveUserToken(savedUser, jwtToken);
        return RegisterResponse.builder()
                .id(user.getNickname())
                .name(user.getName())
                .build();
    }



    public AuthenticationResponse authenticate(RegisterRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNickname(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByNickname(request.getNickname()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        cleanUpTokens(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        var user = jwtService.findUser(request);
        if (user != null){
            var accessToken = jwtService.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, accessToken);

            String json = new ObjectMapper().writeValueAsString(Collections.singletonMap("accessToken", accessToken));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        }
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response){

        var user = jwtService.findUser(request);

        if (user != null){
            // 하위 테이블 먼저 삭제
            List<Token> tokens = tokenRepository.findTotalTokenByUserId(user.getId());
            if (tokens != null){
                tokenRepository.deleteAll(tokens);
            }

            this.userRepository.delete(user);
            response.setStatus(HttpStatus.OK.value());
        }
    }

    // token 저장
    private void saveUserToken(User user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    // 모든 token 폐기
    private void revokeAllUserTokens(User user){
        var validUserTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    // 만료된 token DB 제거
    private void cleanUpTokens(User user){
        var expiredTokens = tokenRepository.findAllExpiredTokensByUserId(user.getId());
        tokenRepository.deleteAll(expiredTokens);
    }

}
