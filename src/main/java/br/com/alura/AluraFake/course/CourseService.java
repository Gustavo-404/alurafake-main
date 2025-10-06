package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.MultipleChoiceTask;
import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.task.SingleChoiceTask;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Course createCourse(NewCourseDTO newCourse) {
        User instructor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor)
                .orElseThrow(() -> new BusinessRuleException("Usuário não encontrado ou não é um instrutor"));

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), instructor);
        return courseRepository.save(course);
    }

    public List<CourseListItemDTO> findAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
    }

    @Transactional
    public void publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        if (course.getStatus() != Status.BUILDING) {
            throw new BusinessRuleException("O curso só pode ser publicado se o status for BUILDING.");
        }

        List<Task> tasks = taskRepository.findAllByCourseIdOrderByOrderAsc(courseId);
        if (tasks.isEmpty()) {
            throw new BusinessRuleException("O curso deve ter ao menos uma atividade para ser publicado.");
        }

        validateTaskTypes(tasks);
        validateTaskOrderSequence(tasks);

        course.publish();
        courseRepository.save(course);
    }

    private void validateTaskTypes(List<Task> tasks) {
        Set<Class<? extends Task>> taskTypes = tasks.stream()
                .map(Task::getClass)
                .collect(Collectors.toSet());

        boolean hasAllTypes = taskTypes.contains(OpenTextTask.class) &&
                taskTypes.contains(SingleChoiceTask.class) &&
                taskTypes.contains(MultipleChoiceTask.class);

        if (!hasAllTypes) {
            throw new BusinessRuleException("Para ser publicado, o curso deve conter ao menos uma atividade de cada tipo (Resposta Aberta, Alternativa Única e Múltipla Escolha).");
        }
    }

    private void validateTaskOrderSequence(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getOrder() != (i + 1)) {
                throw new BusinessRuleException("As atividades do curso não estão em sequência contínua (ex: 1, 2, 3...).");
            }
        }
    }
}