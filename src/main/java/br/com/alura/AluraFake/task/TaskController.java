package br.com.alura.AluraFake.task;

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
    public ResponseEntity<Void> newOpenTextTask(@RequestBody @Valid NewOpenTextTaskDTO dto, JwtAuthenticationToken token) {
        taskService.createTask(dto, Long.parseLong(token.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity<Void> newSingleChoiceTask(@RequestBody @Valid NewSingleChoiceTaskDTO dto, JwtAuthenticationToken token) {
        taskService.createTask(dto, Long.parseLong(token.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity<Void> newMultipleChoiceTask(@RequestBody @Valid NewMultipleChoiceTaskDTO dto, JwtAuthenticationToken token) {
        taskService.createTask(dto, Long.parseLong(token.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}