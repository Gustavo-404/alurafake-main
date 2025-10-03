package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import org.springframework.util.Assert;

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
        this.options = options;
    }

    private void validateOptions(List<Option> options) {
        Assert.notEmpty(options, "A lista de opções não pode ser vazia.");
        Assert.isTrue(options.size() >= 2 && options.size() <= 5, "A atividade deve ter entre 2 e 5 alternativas.");

        long correctOptionsCount = options.stream().filter(Option::isCorrect).count();
        Assert.isTrue(correctOptionsCount == 1, "A atividade de alternativa única deve ter exatamente uma alternativa correta.");
    }
}