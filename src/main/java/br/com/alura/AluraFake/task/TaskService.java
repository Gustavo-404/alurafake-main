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
    private final TaskFactory taskFactory;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository, TaskFactory taskFactory) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
        this.taskFactory = taskFactory;
    }

    @Transactional
    public void createTask(NewTaskDTO dto) {
        Course course = prepareTaskCreation(dto.getCourseId(), dto.getOrder(), dto.getStatement());
        Task task = taskFactory.createTask(dto, course);
        taskRepository.save(task);
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
        if (isOrderGreaterThanLast) {
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