package br.com.alura.AluraFake.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void newOpenTextTask__should_return_created_when_request_is_valid() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(42L);
        dto.setStatement("O que aprendemos na aula de hoje?");
        dto.setOrder(1);

        doNothing().when(taskService).createTask(any(NewOpenTextTaskDTO.class));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(NewOpenTextTaskDTO.class));
    }

    @Test
    void newOpenTextTask__should_return_bad_request_when_statement_is_blank() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("");
        dto.setOrder(1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field", containsInAnyOrder("statement", "statement")))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder("must not be blank", "length must be between 4 and 255")));
    }

    @Test
    void newOpenTextTask__should_return_bad_request_when_courseId_is_null() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setCourseId(null);
        dto.setStatement("O que aprendemos na aula de hoje?");
        dto.setOrder(1);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").value("must not be null"));
    }


    // Testes para /task/new/singlechoice
    @Test
    void newSingleChoiceTask__should_return_created_when_request_is_valid() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(42L);
        dto.setStatement("O que aprendemos hoje?");

        dto.setOrder(2);
        NewOptionDTO option1 = new NewOptionDTO();
        option1.setText("Java");
        option1.setIsCorrect(true);
        NewOptionDTO option2 = new NewOptionDTO();
        option2.setText("Python");
        option2.setIsCorrect(false);
        NewOptionDTO option3 = new NewOptionDTO();
        option3.setText("Ruby");
        option3.setIsCorrect(false);
        dto.setOptions(List.of(option1, option2, option3));

        doNothing().when(taskService).createTask(any(NewSingleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(NewSingleChoiceTaskDTO.class));
    }

    @Test
    void newSingleChoiceTask__should_return_bad_request_when_options_list_is_too_small() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("O que aprendemos na aula de hoje?");
        dto.setOrder(2);
        dto.setOptions(Collections.emptyList());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").value("size must be between 2 and 5"));
    }

    @Test
    void newSingleChoiceTask__should_return_bad_request_when_nested_option_is_invalid() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("O que aprendemos na aula de hoje?");
        dto.setOrder(2);
        NewOptionDTO option1 = new NewOptionDTO();
        option1.setText("");
        option1.setIsCorrect(true);
        NewOptionDTO option2 = new NewOptionDTO();
        option2.setText("Java");
        option2.setIsCorrect(false);
        dto.setOptions(List.of(option1, option2));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].field", containsInAnyOrder("options[0].text", "options[0].text")))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder("must not be blank", "length must be between 4 and 80")));
    }


    // Testes para /task/new/multiplechoice
    @Test
    void newMultipleChoiceTask__should_return_created_when_request_is_valid() throws Exception {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(42L);
        dto.setStatement("O que aprendemos hoje?");
        dto.setOrder(2);

        NewOptionDTO option1 = new NewOptionDTO();
        option1.setText("Java");
        option1.setIsCorrect(true);

        NewOptionDTO option2 = new NewOptionDTO();
        option2.setText("Spring");
        option2.setIsCorrect(true);

        NewOptionDTO option3 = new NewOptionDTO();
        option3.setText("Ruby");
        option3.setIsCorrect(false);

        dto.setOptions(List.of(option1, option2, option3));

        doNothing().when(taskService).createTask(any(NewMultipleChoiceTaskDTO.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(NewMultipleChoiceTaskDTO.class));
    }

    @Test
    void newMultipleChoiceTask__should_return_bad_request_when_options_list_is_too_small() throws Exception {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("O que aprendemos hoje?");
        dto.setOrder(3);

        NewOptionDTO option1 = new NewOptionDTO();
        option1.setText("Java");
        option1.setIsCorrect(true);

        NewOptionDTO option2 = new NewOptionDTO();
        option2.setText("Spring");
        option2.setIsCorrect(true);

        dto.setOptions(List.of(option1, option2));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").value("size must be between 3 and 5"));
    }
}