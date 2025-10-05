package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import jakarta.persistence.*;

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
        if (options == null || options.isEmpty()) {
            throw new BusinessRuleException("A lista de opções não pode ser vazia.");
        }
        if (options.size() < 3 || options.size() > 5) {
            throw new BusinessRuleException("A atividade deve ter entre 3 e 5 alternativas.");
        }

        long correctOptionsCount = options.stream().filter(Option::isCorrect).count();
        if (correctOptionsCount < 2) {
            throw new BusinessRuleException("A atividade de múltipla escolha deve ter duas ou mais alternativas corretas.");
        }

        long incorrectOptionsCount = options.size() - correctOptionsCount;
        if (incorrectOptionsCount < 1) {
            throw new BusinessRuleException("A atividade de múltipla escolha deve ter ao menos uma alternativa incorreta.");
        }

        Set<String> optionTexts = new HashSet<>();
        for (Option option : options) {
            if (option.getText().equalsIgnoreCase(getStatement())) {
                throw new BusinessRuleException("A alternativa não pode ser igual ao enunciado.");
            }
            if (!optionTexts.add(option.getText())) {
                throw new BusinessRuleException("As alternativas não podem ser iguais entre si.");
            }
        }
    }
}