package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.alura.AluraFake.user.Role.INSTRUCTOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskFactory taskFactory;

    @InjectMocks
    private TaskService taskService;

    private Course course;
    private User instructor;

    private Long instructorId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instructor = spy(new User("Paulo", "paulo@alura.com.br", INSTRUCTOR));
        when(instructor.getId()).thenReturn(instructorId);
        course = new Course("Java", "Aprenda Java com Alura", instructor);
    }


    @Test
    void createTask__should_save_task_without_reordering_when_order_is_free() {
        Course spiedCourse = spy(course);
        when(spiedCourse.getId()).thenReturn(1L);

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("New Task");
        dto.setOrder(1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(spiedCourse));
        when(taskRepository.existsByCourseIdAndStatement(1L, "New Task")).thenReturn(false);
        when(taskRepository.existsByCourseIdAndOrder(1L, 1)).thenReturn(false);
        when(taskRepository.existsByCourseIdAndOrder(1L, 0)).thenReturn(false);

        OpenTextTask expectedTask = new OpenTextTask(spiedCourse, dto.getStatement(), dto.getOrder());
        when(taskFactory.createTask(dto, spiedCourse)).thenReturn(expectedTask);

        taskService.createTask(dto, instructorId);

        verify(taskRepository, never()).findAllByCourseIdAndOrderGreaterThanEqualOrderByOrderDesc(eq(instructorId), anyInt());
        verify(taskRepository, never()).saveAll(any());
        verify(taskRepository).save(any(OpenTextTask.class));
    }

    @Test
    void createTask__should_reorder_and_save_task_when_order_is_taken() {
        Course spiedCourse = spy(course);
        when(spiedCourse.getId()).thenReturn(1L);

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Another Task");
        dto.setOrder(1);

        Task existingTask = new OpenTextTask(spiedCourse, "Existing Task", 1);
        List<Task> tasksToShift = Collections.singletonList(existingTask);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(spiedCourse));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Another Task")).thenReturn(false);
        when(taskRepository.existsByCourseIdAndOrder(1L, 1)).thenReturn(true);
        when(taskRepository.existsByCourseIdAndOrder(1L, 0)).thenReturn(true);
        when(taskRepository.findAllByCourseIdAndOrderGreaterThanEqualOrderByOrderDesc(1L, 1)).thenReturn(tasksToShift);

        OpenTextTask expectedTask = new OpenTextTask(spiedCourse, dto.getStatement(), dto.getOrder());
        when(taskFactory.createTask(dto, spiedCourse)).thenReturn(expectedTask);
        taskService.createTask(dto, instructorId);

        verify(taskRepository).findAllByCourseIdAndOrderGreaterThanEqualOrderByOrderDesc(1L, 1);

        ArgumentCaptor<List<Task>> captor = ArgumentCaptor.forClass(List.class);
        verify(taskRepository).saveAll(captor.capture());

        List<Task> capturedTasks = captor.getValue();
        assertEquals(1, capturedTasks.size());
        assertEquals(2, capturedTasks.get(0).getOrder());

        verify(taskRepository).save(any(OpenTextTask.class));
    }


    @Test
    void createTask__should_throw_ResourceNotFoundException_when_course_does_not_exist() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(99L);

        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(dto, instructorId);
        }, "Curso não encontrado");
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_course_is_not_building() {
        course.setStatus(Status.PUBLISHED);
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Statement");
        dto.setOrder(1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(dto, instructorId);
        });
        assertEquals("Só é possível adicionar atividades em cursos com status BUILDING", exception.getMessage());
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_statement_already_exists() {
        Course spiedCourse = spy(course);
        when(spiedCourse.getId()).thenReturn(1L);

        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Existing Statement");
        dto.setOrder(1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(spiedCourse));
        when(taskRepository.existsByCourseIdAndStatement(1L, "Existing Statement")).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(dto, instructorId);
        });
        assertEquals("O curso já possui uma atividade com este enunciado.", exception.getMessage());
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_order_sequence_is_broken() {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("New Task");
        dto.setOrder(3);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseIdAndStatement(1L, "New Task")).thenReturn(false);

        when(taskRepository.countByCourseId(1L)).thenReturn(1L);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(dto, instructorId);
        });
        assertEquals("A ordem das atividades deve ser contínua, sem saltos.", exception.getMessage());
    }
}