package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

public class NewUserDTO {

    @NotNull
    @Length(min = 3, max = 50)
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotNull
    private Role role;
    @NotBlank
    @Pattern(regexp = "^.{6}$", message = "A senha deve ter exatamente 6 caracteres")
    private String password;

    public NewUserDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public User toModel() {
        return new User(name, email, role);
    }

    public User toModelWithPassword(PasswordEncoder passwordEncoder) {
        return new User(name, email, role, passwordEncoder.encode(password));
    }

}
