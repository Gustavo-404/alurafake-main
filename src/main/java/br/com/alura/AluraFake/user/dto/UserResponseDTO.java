package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        LocalDateTime createdAt,
        String name,
        String email,
        Role role
) {
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getCreatedAt(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}