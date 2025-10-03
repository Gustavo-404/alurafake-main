package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewOptionDTO {

    @NotBlank
    @Length(min = 4, max = 80)
    private String text;

    @NotNull
    private Boolean isCorrect;

    public String getText() {
        return text;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }
}