package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Task")
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends Task {

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    @Deprecated
    public MultipleChoiceTask() {}

    public MultipleChoiceTask(Course course, String statement, int order, List<Option> options) {
        super(course, statement, order);

        validateOptions(options);
        options.forEach(option -> option.setTask(this));

        this.options = options;
    }

    private void validateOptions(List<Option> options) {
        Assert.notEmpty(options, "A lista de opções não pode ser vazia.");
        Assert.isTrue(options.size() >= 3 && options.size() <= 5, "A atividade deve ter entre 3 e 5 alternativas.");

        long correctOptionsCount = options.stream().filter(Option::isCorrect).count();
        Assert.isTrue(correctOptionsCount >= 2, "A atividade de múltipla escolha deve ter duas ou mais alternativas corretas.");

        long incorrectOptionsCount = options.size() - correctOptionsCount;
        Assert.isTrue(incorrectOptionsCount >= 1, "A atividade de múltipla escolha deve ter ao menos uma alternativa incorreta.");

        Set<String> optionTexts = new HashSet<>();
        for (Option option : options) {
            Assert.isTrue(!option.getText().equalsIgnoreCase(getStatement()), "A alternativa não pode ser igual ao enunciado.");
            Assert.isTrue(optionTexts.add(option.getText()), "As alternativas não podem ser iguais entre si.");
        }
    }
}