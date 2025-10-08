package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.task.model.OpenTextTask;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.user.service.InstructorService;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class InstructorServiceIntegrationTest {

    @Autowired private InstructorService instructorService;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private TaskRepository taskRepository;

    private User instructor;
    private User student;

    @BeforeEach
    void setUp() {
        instructor = userRepository.save(new User("Instructor", "instructor.report@test.com", Role.INSTRUCTOR));
        student = userRepository.save(new User("Student", "student.report@test.com", Role.STUDENT));
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void generateReport__should_return_correct_report_for_valid_instructor() {

        Course c1 = courseRepository.save(new Course("Java Basics", "...", instructor));
        c1.publish();
        courseRepository.save(c1);

        Course c2 = courseRepository.save(new Course("Spring Boot", "...", instructor));

        taskRepository.save(new OpenTextTask(c2, "Task 1 for Spring", 1));
        taskRepository.save(new OpenTextTask(c2, "Task 2 for Spring", 2));

        var report = instructorService.generateReport(instructor.getId(), PageRequest.of(0, 5));

        assertThat(report.getTotalPublishedCourses()).isEqualTo(1);
        assertThat(report.getCoursesPage().getTotalElements()).isEqualTo(2);

        var course1Report = report.getCoursesPage().getContent().stream().filter(c -> c.getTitle().equals("Java Basics")).findFirst().get();
        assertThat(course1Report.getStatus()).isEqualTo(Status.PUBLISHED);
        assertThat(course1Report.getTaskCount()).isEqualTo(0);

        var course2Report = report.getCoursesPage().getContent().stream().filter(c -> c.getTitle().equals("Spring Boot")).findFirst().get();
        assertThat(course2Report.getStatus()).isEqualTo(Status.BUILDING);
        assertThat(course2Report.getTaskCount()).isEqualTo(2);
    }

    @Test
    void generateReport__should_throw_ResourceNotFoundException_for_non_existent_user() {
        assertThrows(ResourceNotFoundException.class, () -> {
            instructorService.generateReport(999L, PageRequest.of(0, 5));
        });
    }

    @Test
    void generateReport__should_throw_BusinessRuleException_for_user_who_is_not_instructor() {
        assertThrows(BusinessRuleException.class, () -> {
            instructorService.generateReport(student.getId(), PageRequest.of(0, 5));
        });
    }

    @Test
    void generateReport__should_return_empty_report_for_instructor_with_no_courses() {
        var report = instructorService.generateReport(instructor.getId(), PageRequest.of(0, 5));
        assertThat(report.getTotalPublishedCourses()).isEqualTo(0);
        assertThat(report.getCoursesPage().isEmpty()).isTrue();
    }
}