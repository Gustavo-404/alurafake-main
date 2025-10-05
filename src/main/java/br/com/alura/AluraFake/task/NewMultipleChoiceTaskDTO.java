package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class NewMultipleChoiceTaskDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    @Length(min = 4, max = 255)
    private String statement;

    @NotNull
    @Min(1)
    private Integer order;

    @NotNull
    @Valid
    private List<NewOptionDTO> options;

    public Long getCourseId() {
        return courseId;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }

    public List<NewOptionDTO> getOptions() {
        return options;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setOptions(List<NewOptionDTO> options) {
        this.options = options;
    }
}