package br.com.alura.AluraFake.auth;

import br.com.alura.AluraFake.auth.controller.TokenController;
import br.com.alura.AluraFake.auth.dto.LoginRequestDTO;
import br.com.alura.AluraFake.config.SecurityConfig;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenController.class)
@Import(SecurityConfig.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtEncoder jwtEncoder;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserRepository userRepository;


    @Test
    void login__should_return_token_when_credentials_are_valid() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@instructor.com", "password123");
        User mockUser = new User("Test Instructor", "test@instructor.com", Role.INSTRUCTOR);
        mockUser.setId(1L);
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(mockUser));
        when(bCryptPasswordEncoder.matches(loginRequestDTO.password(), mockUser.getPassword())).thenReturn(true);

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mock.jwt.token.value");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token.value"))
                .andExpect(jsonPath("$.expiresIn").value(300L));
    }

    @Test
    void login__should_return_unauthorized_when_user_not_found() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("nonexistent@user.com", "password123");
        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login__should_return_unauthorized_when_password_is_incorrect() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@instructor.com", "wrongPassword");
        User mockUser = new User("Test Instructor", "test@instructor.com", Role.INSTRUCTOR);
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(mockUser));
        when(bCryptPasswordEncoder.matches(loginRequestDTO.password(), mockUser.getPassword())).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized());
    }
}
