package br.com.alura.AluraFake.task.model;

import br.com.alura.AluraFake.course.model.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Task")
@DiscriminatorValue("OPEN_TEXT")
public class OpenTextTask extends Task {

    @Deprecated
    public OpenTextTask() {}

    public OpenTextTask(Course course, String statement, int order) {
        super(course, statement, order);
    }
}