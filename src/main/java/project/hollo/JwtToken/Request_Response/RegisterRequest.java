package project.hollo.JwtToken.Request_Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.hollo.User.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nickname;
    private String password;
    private String name;
    private String birthDay;
    private Role role;
}
