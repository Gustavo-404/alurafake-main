package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOptionDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.model.MultipleChoiceTask;
import br.com.alura.AluraFake.task.model.OpenTextTask;
import br.com.alura.AluraFake.task.model.Option;
import br.com.alura.AluraFake.task.model.SingleChoiceTask;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class CourseServiceIntegrationTest {

    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskService taskService;

    private Course course;
    private User instructor;

    @BeforeEach
    void setUp() {
        instructor = new User("Test Instructor", "instructor-publish@test.com", Role.INSTRUCTOR);
        userRepository.save(instructor);
        course = new Course("Test Course", "Description", instructor);
        courseRepository.save(course);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void publishCourse__should_publish_successfully_when_all_rules_are_met() {
        createSampleTasks();

        courseService.publishCourse(course.getId(), instructor.getId());

        Course publishedCourse = courseRepository.findById(course.getId()).get();
        assertThat(publishedCourse.getStatus()).isEqualTo(Status.PUBLISHED);
        assertThat(publishedCourse.getPublishedAt()).isNotNull();
    }

    @Test
    void publishCourse__should_throw_exception_when_course_status_is_not_building() {
        createSampleTasks();
        course.publish();
        courseRepository.save(course);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            courseService.publishCourse(course.getId(), instructor.getId());
        });
        assertEquals("O curso só pode ser publicado se o status for BUILDING.", exception.getMessage());
    }

    @Test
    void publishCourse__should_throw_exception_when_instructor_tries_to_publish_activity_to_another_instructor_course() {
        User instructor2 = new User("Test Instructor", "instructor2@test.com", Role.INSTRUCTOR);
        userRepository.save(instructor2);
        Course course2 = new Course("Test Course 2", "Description 2", instructor2);
        courseRepository.save(course2);
        taskService.createTask(new NewOpenTextTaskDTO(course2.getId(), "Open Text", 1), instructor2.getId());


        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(new NewSingleChoiceTaskDTO(course.getId(), "Single Choice", 2, List.of(new NewOptionDTO("A", true), new NewOptionDTO("B", false))), instructor2.getId());
            courseService.publishCourse(course.getId(), instructor.getId());
        });
        assertEquals("O instrutor só pode adicionar atividades aos próprios cursos.", exception.getMessage());
    }

    @Test
    void publishCourse__should_throw_exception_when_a_task_type_is_missing() {
        taskService.createTask(new NewOpenTextTaskDTO(course.getId(), "Open Text", 1), instructor.getId());
        taskService.createTask(new NewSingleChoiceTaskDTO(course.getId(), "Single Choice", 2, List.of(new NewOptionDTO("A", true), new NewOptionDTO("B", false))), instructor.getId());

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            courseService.publishCourse(course.getId(), instructor.getId());
        });
        assertEquals("Para ser publicado, o curso deve conter ao menos uma atividade de cada tipo (Resposta Aberta, Alternativa Única e Múltipla Escolha).", exception.getMessage());
    }

    @Test
    void publishCourse__should_throw_exception_when_task_order_is_not_sequential() {
        taskRepository.save(new OpenTextTask(course, "Open Text", 1));
        taskRepository.save(new SingleChoiceTask(course, "Single Choice", 2, List.of(new Option("A", true), new Option("B", false))));
        taskRepository.save(new MultipleChoiceTask(course, "Multiple Choice", 4, List.of(new Option("A", true), new Option("B", true), new Option("C", false))));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            courseService.publishCourse(course.getId(), instructor.getId());
        });
        assertEquals("As atividades do curso não estão em sequência contínua (ex: 1, 2, 3...).", exception.getMessage());
    }

    @Test
    void publishCourse__should_throw_exception_when_course_has_no_tasks() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            courseService.publishCourse(course.getId(), instructor.getId());
        });
        assertEquals("O curso deve ter ao menos uma atividade para ser publicado.", exception.getMessage());
    }

    private void createSampleTasks() {
        taskService.createTask(new NewOpenTextTaskDTO(course.getId(), "Open Text", 1), instructor.getId());
        taskService.createTask(new NewSingleChoiceTaskDTO(course.getId(), "Single Choice", 2, List.of(new NewOptionDTO("A", true), new NewOptionDTO("B", false))), instructor.getId());
        taskService.createTask(new NewMultipleChoiceTaskDTO(course.getId(), "Multiple Choice", 3, List.of(new NewOptionDTO("A", true), new NewOptionDTO("B", true), new NewOptionDTO("C", false))), instructor.getId());
    }
}