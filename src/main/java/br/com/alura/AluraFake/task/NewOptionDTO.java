package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewOptionDTO {

    @NotBlank
    @Length(min = 4, max = 80)
    private String option;

    @NotNull
    private Boolean isCorrect;


    public NewOptionDTO() { }

    public NewOptionDTO(String option, Boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public String getOption() {
        return option;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }
}