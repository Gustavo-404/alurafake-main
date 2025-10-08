package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.controller.TaskController;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOptionDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.model.*;
import br.com.alura.AluraFake.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void newOpenTextTask__should_return_created_when_request_is_valid() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO(42L, "O que aprendemos na aula de hoje?", 1);
        long instructorId = 1L;

        Task returnedTask = new OpenTextTask(null, dto.getStatement(), dto.getOrder());
        returnedTask.setId(1L);

        when(taskService.createTask(any(NewOpenTextTaskDTO.class), eq(instructorId)))
                .thenReturn(returnedTask);

        mockMvc.perform(post("/task/new/opentext")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("scope", "INSTRUCTOR")
                                .subject(String.valueOf(instructorId))
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnedTask.getId()))
                .andExpect(jsonPath("$.statement").value(dto.getStatement()));

        verify(taskService).createTask(any(NewOpenTextTaskDTO.class), eq(instructorId));
    }

    @Test
    void newOpenTextTask__should_return_bad_request_when_statement_is_blank() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("");
        dto.setOrder(1);

        mockMvc.perform(post("/task/new/opentext")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Bad Request"))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Campo 'statement': must not be blank",
                        "Campo 'statement': length must be between 4 and 255"
                )));
    }

    @Test
    void newOpenTextTask__should_return_bad_request_when_courseId_is_null() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(null);
        dto.setStatement("O que aprendemos na aula de hoje?");
        dto.setOrder(1);

        mockMvc.perform(post("/task/new/opentext")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Bad Request"))
                .andExpect(jsonPath("$.errors[0]").value("Campo 'courseId': must not be null"));
    }

    @Test
    void newSingleChoiceTask__should_return_created_when_request_is_valid() throws Exception {
        long instructorId = 1L;

        NewOptionDTO optionDto1 = new NewOptionDTO("Java", true);
        NewOptionDTO optionDto2 = new NewOptionDTO("Python", false);
        NewOptionDTO optionDto3 = new NewOptionDTO("Ruby", false);

        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(42L, "O que aprendemos hoje?", 2, List.of(optionDto1, optionDto2, optionDto3));

        List<Option> options = List.of(
                new Option("Java", true),
                new Option("Python", false),
                new Option("Ruby", false)
        );
        Task returnedTask = new SingleChoiceTask(null, dto.getStatement(), dto.getOrder(), options);
        returnedTask.setId(3L);

        when(taskService.createTask(any(NewSingleChoiceTaskDTO.class), eq(instructorId)))
                .thenReturn(returnedTask);

        mockMvc.perform(post("/task/new/singlechoice")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("scope", "INSTRUCTOR")
                                .subject(String.valueOf(instructorId))
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnedTask.getId()))
                .andExpect(jsonPath("$.options.length()").value(3))
                .andExpect(jsonPath("$.options[0].text").value("Java"))
                .andExpect(jsonPath("$.options[0].isCorrect").value(true));

        verify(taskService).createTask(any(NewSingleChoiceTaskDTO.class), eq(instructorId));
    }

    @Test
    void newSingleChoiceTask__should_return_bad_request_when_nested_option_is_invalid() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("O que aprendemos na aula de hoje?");
        dto.setOrder(2);
        NewOptionDTO option1 = new NewOptionDTO();
        option1.setOption("");
        option1.setIsCorrect(true);
        NewOptionDTO option2 = new NewOptionDTO();
        option2.setOption("Java");
        option2.setIsCorrect(false);
        dto.setOptions(List.of(option1, option2));

        mockMvc.perform(post("/task/new/singlechoice")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", containsInAnyOrder(
                        "Campo 'options[0].option': must not be blank",
                        "Campo 'options[0].option': length must be between 4 and 80"
                )));
    }

    @Test
    void newMultipleChoiceTask__should_return_created_when_request_is_valid() throws Exception {
        long instructorId = 1L;

        NewOptionDTO optionDto1 = new NewOptionDTO("Java", true);
        NewOptionDTO optionDto2 = new NewOptionDTO("Spring", true);
        NewOptionDTO optionDto3 = new NewOptionDTO("Ruby", false);

        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(42L, "O que aprendemos hoje?", 2, List.of(optionDto1, optionDto2, optionDto3));

        List<Option> options = List.of(
                new Option("Java", true),
                new Option("Spring", true),
                new Option("Ruby", false)
        );
        Task returnedTask = new MultipleChoiceTask(null, dto.getStatement(), dto.getOrder(), options);
        returnedTask.setId(2L);

        when(taskService.createTask(any(NewMultipleChoiceTaskDTO.class), eq(instructorId)))
                .thenReturn(returnedTask);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("scope", "INSTRUCTOR")
                                .subject(String.valueOf(instructorId))
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returnedTask.getId()))
                .andExpect(jsonPath("$.statement").value(dto.getStatement()))
                .andExpect(jsonPath("$.options.length()").value(3))
                .andExpect(jsonPath("$.options[0].text").value("Java"))
                .andExpect(jsonPath("$.options[0].isCorrect").value(true));

        verify(taskService).createTask(any(NewMultipleChoiceTaskDTO.class), eq(instructorId));
    }

}