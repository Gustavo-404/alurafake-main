package br.com.alura.AluraFake.util.exception;

import org.springframework.validation.FieldError;

public class ErrorItemDTO {

    private final String field;
    private final String message;

    public ErrorItemDTO(FieldError fieldError) {
        this(fieldError.getField(), fieldError.getDefaultMessage());
    }

    public ErrorItemDTO(String field, String message) {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException("O campo 'field' não pode ser nulo ou vazio ao criar um ErrorItemDTO.");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("O campo 'message' não pode ser nulo ou vazio ao criar um ErrorItemDTO.");
        }
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
