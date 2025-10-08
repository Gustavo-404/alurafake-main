package br.com.alura.AluraFake.course.dto;
import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.model.Course;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public record NewCourseResponseDTO(
        Long id,
        LocalDateTime createdAt,
        String title,
        String description,
        Status status,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDateTime publishedAt
) {
    public NewCourseResponseDTO(Course course) {
        this(
                course.getId(),
                course.getCreatedAt(),
                course.getTitle(),
                course.getDescription(),
                course.getStatus(),
                course.getPublishedAt()
        );
    }
}