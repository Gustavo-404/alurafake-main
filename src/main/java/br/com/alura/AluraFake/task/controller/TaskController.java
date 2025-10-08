package br.com.alura.AluraFake.task.controller;

import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewTaskResponseDTO;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.task.dto.NewTaskWithOptionsResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/task/new/opentext")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity<NewTaskResponseDTO> newOpenTextTask(@RequestBody @Valid NewOpenTextTaskDTO dto, JwtAuthenticationToken token) {
        Task task = taskService.createTask(dto, Long.parseLong(token.getName()));
        NewTaskResponseDTO responseDto = new NewTaskResponseDTO(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/task/new/singlechoice")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity<NewTaskWithOptionsResponseDTO> newSingleChoiceTask(@RequestBody @Valid NewSingleChoiceTaskDTO dto, JwtAuthenticationToken token) {
        Task task = taskService.createTask(dto, Long.parseLong(token.getName()));
        NewTaskWithOptionsResponseDTO responseDto = new NewTaskWithOptionsResponseDTO(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    @PostMapping("/task/new/multiplechoice")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity<NewTaskWithOptionsResponseDTO> newMultipleChoiceTask(@RequestBody @Valid NewMultipleChoiceTaskDTO dto, JwtAuthenticationToken token) {
        Task task = taskService.createTask(dto, Long.parseLong(token.getName()));
        NewTaskWithOptionsResponseDTO responseDto = new NewTaskWithOptionsResponseDTO(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}