package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Só é possível adicionar atividades em cursos com status BUILDING");
        }

        if (taskRepository.existsByCourseIdAndStatement(course.getId(), dto.getStatement())) {
            throw new IllegalArgumentException("O curso já possui uma atividade com este enunciado.");
        }

        boolean isOrderGreaterThanLast = !taskRepository.existsByCourseIdAndOrder(course.getId(), dto.getOrder() - 1) && dto.getOrder() > 1;
        if(isOrderGreaterThanLast) {
            throw new IllegalStateException("A ordem das atividades deve ser contínua, sem saltos.");
        }

        if (taskRepository.existsByCourseIdAndOrder(course.getId(), dto.getOrder())) {
            taskRepository.shiftOrdersForward(course.getId(), dto.getOrder());
        }

        OpenTextTask openTextTask = new OpenTextTask(course, dto.getStatement(), dto.getOrder());
        taskRepository.save(openTextTask);
    }
}