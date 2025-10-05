package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void createTask(NewOpenTextTaskDTO dto) {
        Course course = prepareTaskCreation(dto.getCourseId(), dto.getOrder(), dto.getStatement());
        OpenTextTask openTextTask = new OpenTextTask(course, dto.getStatement(), dto.getOrder());
        taskRepository.save(openTextTask);
    }

    @Transactional
    public void createTask(NewSingleChoiceTaskDTO dto) {
        Course course = prepareTaskCreation(dto.getCourseId(), dto.getOrder(), dto.getStatement());

        List<Option> options = dto.getOptions().stream()
                .map(optionDto -> new Option(optionDto.getOption(), optionDto.getIsCorrect()))
                .collect(Collectors.toList());

        SingleChoiceTask singleChoiceTask = new SingleChoiceTask(course, dto.getStatement(), dto.getOrder(), options);

        taskRepository.save(singleChoiceTask);
    }

    @Transactional
    public void createTask(NewMultipleChoiceTaskDTO dto) {
        Course course = prepareTaskCreation(dto.getCourseId(), dto.getOrder(), dto.getStatement());

        List<Option> options = dto.getOptions().stream()
                .map(optionDto -> new Option(optionDto.getOption(), optionDto.getIsCorrect()))
                .collect(Collectors.toList());

        MultipleChoiceTask multipleChoiceTask = new MultipleChoiceTask(course, dto.getStatement(), dto.getOrder(), options);

        taskRepository.save(multipleChoiceTask);
    }

    private Course prepareTaskCreation(Long courseId, Integer order, String statement) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        if (course.getStatus() != Status.BUILDING) {
            throw new BusinessRuleException("Só é possível adicionar atividades em cursos com status BUILDING");
        }

        if (taskRepository.existsByCourseIdAndStatement(course.getId(), statement)) {
            throw new BusinessRuleException("O curso já possui uma atividade com este enunciado.");
        }

        boolean isOrderGreaterThanLast = !taskRepository.existsByCourseIdAndOrder(course.getId(), order - 1) && order > 1;
        if(isOrderGreaterThanLast) {
            throw new BusinessRuleException("A ordem das atividades deve ser contínua, sem saltos.");
        }

        if (taskRepository.existsByCourseIdAndOrder(course.getId(), order)) {
            taskRepository.shiftOrdersForward(course.getId(), order);
        }

        return course;
    }
}