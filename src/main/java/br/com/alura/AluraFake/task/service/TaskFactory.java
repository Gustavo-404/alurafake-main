package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewTaskDTO;
import br.com.alura.AluraFake.task.model.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskFactory {

    public Task createTask(NewTaskDTO dto, Course course) {
        if (dto instanceof NewOpenTextTaskDTO) {
            return new OpenTextTask(course, dto.getStatement(), dto.getOrder());

        } else if (dto instanceof NewSingleChoiceTaskDTO singleChoiceDto) {
            List<Option> options = singleChoiceDto.getOptions().stream()
                    .map(optionDto -> new Option(optionDto.getOption(), optionDto.getIsCorrect()))
                    .collect(Collectors.toList());
            return new SingleChoiceTask(course, dto.getStatement(), dto.getOrder(), options);

        } else if (dto instanceof NewMultipleChoiceTaskDTO multipleChoiceDto) {
            List<Option> options = multipleChoiceDto.getOptions().stream()
                    .map(optionDto -> new Option(optionDto.getOption(), optionDto.getIsCorrect()))
                    .collect(Collectors.toList());
            return new MultipleChoiceTask(course, dto.getStatement(), dto.getOrder(), options);
        }

        throw new IllegalArgumentException("Tipo de DTO de tarefa desconhecido: " + dto.getClass().getName());
    }
}
