package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.model.Task;

public record NewTaskResponseDTO(
        Long id,
        String statement,
        Integer order
) {
    public NewTaskResponseDTO(Task task) {
        this(
                task.getId(),
                task.getStatement(),
                task.getOrder()
        );
    }
}