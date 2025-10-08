package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.dto.NewTaskDTO;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;
    private final TaskFactory taskFactory;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository, TaskFactory taskFactory) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.taskFactory = taskFactory;
    }

    @Transactional
    public void createTask(NewTaskDTO dto, long instructorId) {
        Course course = prepareTaskCreation(dto.getCourseId(), dto.getOrder(), dto.getStatement(), instructorId);
        Task task = taskFactory.createTask(dto, course);
        taskRepository.save(task);
    }

    private Course prepareTaskCreation(Long courseId, Integer order, String statement, long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        if (!Objects.equals(instructorId, course.getInstructor().getId())) {
            throw new BusinessRuleException("O instrutor só pode adicionar atividades aos próprios cursos.");
        }

        if (course.getStatus() != Status.BUILDING) {
            throw new BusinessRuleException("Só é possível adicionar atividades em cursos com status BUILDING");
        }

        if (taskRepository.existsByCourseIdAndStatement(course.getId(), statement)) {
            throw new BusinessRuleException("O curso já possui uma atividade com este enunciado.");
        }

        long totalTasks = taskRepository.countByCourseId(courseId);
        if (order > totalTasks + 1) {
            throw new BusinessRuleException("A ordem das atividades deve ser contínua, sem saltos.");
        }

        if (taskRepository.existsByCourseIdAndOrder(course.getId(), order)) {
            List<Task> tasksToShift = taskRepository.findAllByCourseIdAndOrderGreaterThanEqualOrderByOrderDesc(course.getId(), order);
            for (Task task : tasksToShift) {
                task.setOrder(task.getOrder() + 1);
            }
            taskRepository.saveAll(tasksToShift);
        }

        return course;
    }
}