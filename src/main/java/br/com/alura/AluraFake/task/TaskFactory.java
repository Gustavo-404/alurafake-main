package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
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
