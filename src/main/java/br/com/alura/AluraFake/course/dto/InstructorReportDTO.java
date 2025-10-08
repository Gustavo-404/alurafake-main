package br.com.alura.AluraFake.course.dto;

import org.springframework.data.domain.Page;

public class InstructorReportDTO {

    private long totalPublishedCourses;
    private Page<CourseReportItemDTO> coursesPage;

    public InstructorReportDTO(long totalPublishedCourses, Page<CourseReportItemDTO> coursesPage) {
        this.totalPublishedCourses = totalPublishedCourses;
        this.coursesPage = coursesPage;
    }

    public long getTotalPublishedCourses() { return totalPublishedCourses; }
    public Page<CourseReportItemDTO> getCoursesPage() { return coursesPage; }
}