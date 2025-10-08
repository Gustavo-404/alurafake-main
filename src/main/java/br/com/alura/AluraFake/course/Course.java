package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.MultipleChoiceTask;
import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.task.SingleChoiceTask;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String title;
    private String description;
    @ManyToOne
    private User instructor;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime publishedAt;

    @Deprecated
    public Course(){}

    public Course(String title, String description, User instructor) {
        if (!instructor.isInstructor()) {
            throw new BusinessRuleException("Usuario deve ser um instrutor");
        }
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void publish() {
        this.status = Status.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void prepareToPublish(List<Task> tasks, Long instructorId) {
        if (!Objects.equals(instructorId, this.instructor.getId())) {
            throw new BusinessRuleException("O instrutor só pode publicar seus próprios cursos.");
        }
        if (this.status != Status.BUILDING) {
            throw new BusinessRuleException("O curso só pode ser publicado se o status for BUILDING.");
        }
        if (tasks.isEmpty()) {
            throw new BusinessRuleException("O curso deve ter ao menos uma atividade para ser publicado.");
        }
        validateTaskTypes(tasks);
        validateTaskOrderSequence(tasks);
        this.publish();
    }

    private void validateTaskTypes(List<Task> tasks) {
        Set<Class<? extends Task>> taskTypes = tasks.stream()
                .map(Task::getClass)
                .collect(Collectors.toSet());

        boolean hasAllTypes = taskTypes.contains(OpenTextTask.class) &&
                taskTypes.contains(SingleChoiceTask.class) &&
                taskTypes.contains(MultipleChoiceTask.class);

        if (!hasAllTypes) {
            throw new BusinessRuleException("Para ser publicado, o curso deve conter ao menos uma atividade de cada tipo (Resposta Aberta, Alternativa Única e Múltipla Escolha).");
        }
    }

    private void validateTaskOrderSequence(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getOrder() != (i + 1)) {
                throw new BusinessRuleException("As atividades do curso não estão em sequência contínua (ex: 1, 2, 3...).");
            }
        }
    }
}
