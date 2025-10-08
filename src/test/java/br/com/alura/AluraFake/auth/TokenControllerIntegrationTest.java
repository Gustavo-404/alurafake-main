package br.com.alura.AluraFake.auth;

import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TokenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void login__should_return_token_when_credentials_are_valid() throws Exception {
        String rawPassword = "password123";
        User instructor = new User(
                "Integration Test Instructor",
                "integration@test.com",
                Role.INSTRUCTOR
        );
        instructor.setPassword(bCryptPasswordEncoder.encode(rawPassword));
        userRepository.save(instructor);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("integration@test.com", rawPassword);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(300L));
    }

    @Test
    void login__should_return_unauthorized_when_password_is_incorrect() throws Exception {
        String rawPassword = "password123";
        User instructor = new User(
                "Integration Test Instructor",
                "integration@test.com",
                Role.INSTRUCTOR
        );
        instructor.setPassword(bCryptPasswordEncoder.encode(rawPassword));
        userRepository.save(instructor);

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("integration@test.com", "wrongPassword");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login__should_return_unauthorized_when_user_does_not_exist() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("nonexistent@user.com", "anyPassword");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized());
    }
}
