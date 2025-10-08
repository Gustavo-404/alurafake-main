package br.com.alura.AluraFake.course.service;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Course createCourse(NewCourseDTO newCourse, long instructorId) {

        User instructor = userRepository
                .findById(instructorId)
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
    public void publishCourse(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        List<Task> tasks = taskRepository.findAllByCourseIdOrderByOrderAsc(courseId);

        course.prepareToPublish(tasks, instructorId);

        courseRepository.save(course);
    }
}