package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.course.CourseReportItemDTO;
import br.com.alura.AluraFake.course.InstructorReportDTO;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorService instructorService;

    @Test
    void getInstructorReport__should_return_ok_when_instructor_is_valid() throws Exception {
        long instructorId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        List<CourseReportItemDTO> courseItems = List.of(
                new CourseReportItemDTO(10L, "Java Completo", Status.PUBLISHED, LocalDateTime.now(), 15L)
        );
        Page<CourseReportItemDTO> coursePage = new PageImpl<>(courseItems, pageable, 1);
        InstructorReportDTO reportDTO = new InstructorReportDTO(1, coursePage);

        when(instructorService.generateReport(eq(instructorId), any(Pageable.class))).thenReturn(reportDTO);

        mockMvc.perform(get("/instructor/{id}/courses", instructorId)
                        .with(jwt())) // Endpoint requer autenticação
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPublishedCourses").value(1))
                .andExpect(jsonPath("$.coursesPage.content[0].title").value("Java Completo"));
    }

    @Test
    void getInstructorReport__should_return_not_found_when_user_does_not_exist() throws Exception {
        long nonExistentId = 999L;
        when(instructorService.generateReport(eq(nonExistentId), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(get("/instructor/{id}/courses", nonExistentId)
                        .with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getInstructorReport__should_return_bad_request_when_user_is_not_instructor() throws Exception {
        long studentId = 2L;

        when(instructorService.generateReport(eq(studentId), any(Pageable.class)))
                .thenThrow(new BusinessRuleException("Usuário não é um instrutor"));

        mockMvc.perform(get("/instructor/{id}/courses", studentId)
                        .with(jwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInstructorReport__should_return_ok_with_empty_list_when_instructor_has_no_courses() throws Exception {
        long instructorId = 3L;
        Pageable pageable = PageRequest.of(0, 5);

        Page<CourseReportItemDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        InstructorReportDTO emptyReport = new InstructorReportDTO(0, emptyPage);

        when(instructorService.generateReport(eq(instructorId), any(Pageable.class))).thenReturn(emptyReport);

        mockMvc.perform(get("/instructor/{id}/courses", instructorId)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPublishedCourses").value(0))
                .andExpect(jsonPath("$.coursesPage.content").isEmpty());
    }
}

