package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> newOpenTextTask(@RequestBody @Valid NewOpenTextTaskDTO dto) {
        taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity<Void> newSingleChoiceTask(@RequestBody @Valid NewSingleChoiceTaskDTO dto) {
        taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity<Void> newMultipleChoiceTask(@RequestBody @Valid NewMultipleChoiceTaskDTO dto) {
        taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}