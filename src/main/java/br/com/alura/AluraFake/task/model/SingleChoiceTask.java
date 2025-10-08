package br.com.alura.AluraFake.task.model;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.service.TaskValidationUtils;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Task")
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends Task {

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    @Deprecated
    public SingleChoiceTask() {}

    public SingleChoiceTask(Course course, String statement, int order, List<Option> options) {
        super(course, statement, order);

        validateOptions(options);
        options.forEach(option -> option.setTask(this));

        this.options = options;
    }

    private void validateOptions(List<Option> options) {

        TaskValidationUtils.validateCommonOptions(options, getStatement());

        if (options.size() < 2 || options.size() > 5) {
            throw new BusinessRuleException("A atividade deve ter entre 2 e 5 alternativas.");
        }

        long correctOptionsCount = options.stream().filter(Option::isCorrect).count();
        if (correctOptionsCount != 1) {
            throw new BusinessRuleException("A atividade de alternativa única deve ter exatamente uma alternativa correta.");
        }
    }
}