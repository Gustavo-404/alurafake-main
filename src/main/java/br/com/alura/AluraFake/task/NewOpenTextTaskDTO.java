package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewOpenTextTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    @Length(min = 4, max = 255)
    private String statement;

    @NotNull
    @Min(1)
    private Integer order;

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }
}