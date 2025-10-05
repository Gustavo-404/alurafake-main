package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Task")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "task_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String statement;

    @Column(name = "task_order", nullable = false)
    private int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Deprecated
    public Task() {}

    public Task(Course course, String statement, int order) {
        this.course = course;
        this.statement = statement;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public int getOrder() {
        return order;
    }

    public Course getCourse() {
        return course;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}