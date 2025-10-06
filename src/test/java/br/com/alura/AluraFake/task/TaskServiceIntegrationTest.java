package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.alura.AluraFake.user.Role.INSTRUCTOR;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private Course savedCourse;

    @BeforeEach
    void setUp() {
        User instructor = new User("Test Instructor", "instructor@test.com", INSTRUCTOR);
        userRepository.save(instructor);
        Course course = new Course("Integration Test Course", "Description", instructor);
        savedCourse = courseRepository.save(course);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createAllTaskTypes__should_save_all_three_task_types_successfully() {
        NewOpenTextTaskDTO openTextDto = new NewOpenTextTaskDTO();
        openTextDto.setCourseId(savedCourse.getId());
        openTextDto.setStatement("First task: Open Text");
        openTextDto.setOrder(1);

        NewSingleChoiceTaskDTO singleChoiceDto = new NewSingleChoiceTaskDTO();
        singleChoiceDto.setCourseId(savedCourse.getId());
        singleChoiceDto.setStatement("Second task: Single Choice");
        singleChoiceDto.setOrder(2);
        NewOptionDTO scOption1 = new NewOptionDTO();
        scOption1.setOption("Correct Option");
        scOption1.setIsCorrect(true);
        NewOptionDTO scOption2 = new NewOptionDTO();
        scOption2.setOption("Wrong Option");
        scOption2.setIsCorrect(false);
        singleChoiceDto.setOptions(List.of(scOption1, scOption2));

        NewMultipleChoiceTaskDTO multipleChoiceDto = new NewMultipleChoiceTaskDTO();
        multipleChoiceDto.setCourseId(savedCourse.getId());
        multipleChoiceDto.setStatement("Third task: Multiple Choice");
        multipleChoiceDto.setOrder(3);
        NewOptionDTO mcOption1 = new NewOptionDTO();
        mcOption1.setOption("Correct 1");
        mcOption1.setIsCorrect(true);
        NewOptionDTO mcOption2 = new NewOptionDTO();
        mcOption2.setOption("Correct 2");
        mcOption2.setIsCorrect(true);
        NewOptionDTO mcOption3 = new NewOptionDTO();
        mcOption3.setOption("Wrong 1");
        mcOption3.setIsCorrect(false);
        multipleChoiceDto.setOptions(List.of(mcOption1, mcOption2, mcOption3));

        taskService.createTask(openTextDto);
        taskService.createTask(singleChoiceDto);
        taskService.createTask(multipleChoiceDto);

        List<Task> tasks = taskRepository.findAll();

        assertThat(tasks).hasSize(3);
        assertThat(tasks).anyMatch(task -> task instanceof OpenTextTask && task.getOrder() == 1);
        assertThat(tasks).anyMatch(task -> task instanceof SingleChoiceTask && task.getOrder() == 2);
        assertThat(tasks).anyMatch(task -> task instanceof MultipleChoiceTask && task.getOrder() == 3);
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_single_choice_has_two_correct_options() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(savedCourse.getId());
        dto.setStatement("Statement");
        dto.setOrder(1);

        NewOptionDTO option1 = new NewOptionDTO();
        option1.setOption("Option 1");
        option1.setIsCorrect(true);
        NewOptionDTO option2 = new NewOptionDTO();
        option2.setOption("Option 2");
        option2.setIsCorrect(true);
        dto.setOptions(List.of(option1, option2));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(dto);
        });

        assertEquals("A atividade de alternativa única deve ter exatamente uma alternativa correta.", exception.getMessage());
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_options_are_duplicated() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(savedCourse.getId());
        dto.setStatement("Statement");
        dto.setOrder(1);

        NewOptionDTO option1 = new NewOptionDTO();
        option1.setOption("Duplicated Text");
        option1.setIsCorrect(true);
        NewOptionDTO option2 = new NewOptionDTO();
        option2.setOption("Duplicated Text");
        option2.setIsCorrect(false);
        dto.setOptions(List.of(option1, option2));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(dto);
        });

        assertEquals("As alternativas não podem ser iguais entre si.", exception.getMessage());
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_option_equals_statement() {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(savedCourse.getId());
        dto.setStatement("Statement");
        dto.setOrder(1);

        NewOptionDTO option1 = new NewOptionDTO();
        option1.setOption("Statement");
        option1.setIsCorrect(true);
        NewOptionDTO option2 = new NewOptionDTO();
        option2.setOption("Another Option");
        option2.setIsCorrect(false);
        dto.setOptions(List.of(option1, option2));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(dto);
        });

        assertEquals("A alternativa não pode ser igual ao enunciado.", exception.getMessage());
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_multiple_choice_has_no_incorrect_option() {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(savedCourse.getId());
        dto.setStatement("Statement");
        dto.setOrder(1);

        NewOptionDTO option1 = new NewOptionDTO();
        option1.setOption("Option 1");
        option1.setIsCorrect(true);
        NewOptionDTO option2 = new NewOptionDTO();
        option2.setOption("Option 2");
        option2.setIsCorrect(true);
        NewOptionDTO option3 = new NewOptionDTO();
        option3.setOption("Option 3");
        option3.setIsCorrect(true);
        dto.setOptions(List.of(option1, option2, option3));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(dto);
        });

        assertEquals("A atividade de múltipla escolha deve ter ao menos uma alternativa incorreta.", exception.getMessage());
    }

    @Test
    void createTask__should_reorder_tasks_when_inserting_in_the_middle() {
        taskService.createTask(createOpenTextDto("Task A", 1));
        taskService.createTask(createOpenTextDto("Task B", 2));
        taskService.createTask(createOpenTextDto("Task C", 3));

        NewOpenTextTaskDTO newTaskDto = createOpenTextDto("New Task at 2", 2);

        taskService.createTask(newTaskDto);

        List<Task> tasks = taskRepository.findAllByCourseIdOrderByOrderAsc(savedCourse.getId());

        assertThat(tasks).hasSize(4);
        assertThat(tasks.get(0).getStatement()).isEqualTo("Task A");
        assertThat(tasks.get(0).getOrder()).isEqualTo(1);

        assertThat(tasks.get(1).getStatement()).isEqualTo("New Task at 2");
        assertThat(tasks.get(1).getOrder()).isEqualTo(2);

        assertThat(tasks.get(2).getStatement()).isEqualTo("Task B");
        assertThat(tasks.get(2).getOrder()).isEqualTo(3);

        assertThat(tasks.get(3).getStatement()).isEqualTo("Task C");
        assertThat(tasks.get(3).getOrder()).isEqualTo(4);
    }

    @Test
    void createTask__should_throw_BusinessRuleException_when_order_sequence_is_broken() {
        taskService.createTask(createOpenTextDto("Task A", 1));
        taskService.createTask(createOpenTextDto("Task B", 2));

        NewOpenTextTaskDTO invalidTaskDto = createOpenTextDto("Invalid Task", 4);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.createTask(invalidTaskDto);
        });

        assertEquals("A ordem das atividades deve ser contínua, sem saltos.", exception.getMessage());
    }
    private NewOpenTextTaskDTO createOpenTextDto(String statement, int order) {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(savedCourse.getId());
        dto.setStatement(statement);
        dto.setOrder(order);
        return dto;
    }
}