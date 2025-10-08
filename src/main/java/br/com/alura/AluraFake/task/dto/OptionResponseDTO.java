package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.model.Option;

public record OptionResponseDTO(
        Long id,
        String text,
        boolean isCorrect
) {
    public OptionResponseDTO(Option option) {
        this(
                option.getId(),
                option.getText(),
                option.isCorrect()
        );
    }
}