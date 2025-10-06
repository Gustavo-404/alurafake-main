package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskValidationUtils {

    public static void validateCommonOptions(List<Option> options, String statement) {
        if (options == null || options.isEmpty()) {
            throw new BusinessRuleException("A lista de opções não pode ser vazia.");
        }

        Set<String> optionTexts = new HashSet<>();
        for (Option option : options) {
            if (option.getText().equalsIgnoreCase(statement)) {
                throw new BusinessRuleException("A alternativa não pode ser igual ao enunciado.");
            }

            if (!optionTexts.add(option.getText())) {
                throw new BusinessRuleException("As alternativas não podem ser iguais entre si.");
            }
        }
    }
}
