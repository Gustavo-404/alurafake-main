package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.model.MultipleChoiceTask;
import br.com.alura.AluraFake.task.model.SingleChoiceTask;
import br.com.alura.AluraFake.task.model.Task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record NewTaskWithOptionsResponseDTO(
        Long id,
        String statement,
        Integer order,
        List<OptionResponseDTO> options
) {
    public NewTaskWithOptionsResponseDTO(Task task) {
        this(
                task.getId(),
                task.getStatement(),
                task.getOrder(),
                extractOptions(task)
        );
    }

    private static List<OptionResponseDTO> extractOptions(Task task) {
        if (task instanceof SingleChoiceTask singleChoiceTask) {
            return singleChoiceTask.getOptions().stream()
                    .map(OptionResponseDTO::new)
                    .collect(Collectors.toList());
        }

        if (task instanceof MultipleChoiceTask multipleChoiceTask) {
            return multipleChoiceTask.getOptions().stream()
                    .map(OptionResponseDTO::new)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
