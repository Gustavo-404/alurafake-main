package br.com.alura.AluraFake.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long>{
    @Query("""
            SELECT new br.com.alura.AluraFake.course.CourseReportItemDTO(
                c.id,
                c.title,
                c.status,
                c.publishedAt,
                COUNT(t.id)
            )
            FROM Course c
            LEFT JOIN Task t ON t.course.id = c.id
            WHERE c.instructor.id = :instructorId
            GROUP BY c.id
            """)
    Page<CourseReportItemDTO> getCourseReportByInstructor(Long instructorId, Pageable pageable);

    long countByInstructorIdAndStatus(Long instructorId, Status status);
}
