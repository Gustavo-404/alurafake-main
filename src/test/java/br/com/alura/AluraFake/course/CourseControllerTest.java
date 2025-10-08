package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.controller.CourseController;
import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    void createCourse__should_return_created_when_request_is_valid() throws Exception {
        long instructorId = 1L;
        NewCourseDTO newCourseDto = new NewCourseDTO();
        newCourseDto.setTitle("Curso de Spring Boot");
        newCourseDto.setDescription("Aprenda do zero");

        Course createdCourse = new Course();
        createdCourse.setId(123L);
        createdCourse.setTitle(newCourseDto.getTitle());
        createdCourse.setDescription(newCourseDto.getDescription());
        when(courseService.createCourse(any(NewCourseDTO.class), eq(instructorId)))
                .thenReturn(createdCourse);

        mockMvc.perform(post("/course/new")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("scope", "INSTRUCTOR")
                                .subject(String.valueOf(instructorId))
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDto)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdCourse.getId()))
                .andExpect(jsonPath("$.title").value("Curso de Spring Boot"));

        verify(courseService).createCourse(any(NewCourseDTO.class), eq(instructorId));
    }

    @Test
    void createCourse__should_return_bad_request_when_service_throws_exception() throws Exception {
        long instructorId = 1L;
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");

        doThrow(new BusinessRuleException("Usuário não encontrado ou não é um instrutor"))
                .when(courseService).createCourse(any(NewCourseDTO.class), anyLong());

        mockMvc.perform(post("/course/new")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("scope", "INSTRUCTOR")
                                .subject(String.valueOf(instructorId))
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Usuário não encontrado ou não é um instrutor"));
    }

    @Test
    void findAllCourses__should_list_all_courses() throws Exception {
        User paulo = new User("Paulo", "paulo@alua.com.br", Role.INSTRUCTOR);
        Course java = new Course("Java", "Curso de java", paulo);
        Course hibernate = new Course("Hibernate", "Curso de hibernate", paulo);

        List<CourseListItemDTO> courseList = Arrays.asList(new CourseListItemDTO(java), new CourseListItemDTO(hibernate));

        when(courseService.findAllCourses()).thenReturn(courseList);

        mockMvc.perform(get("/course/all")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[1].title").value("Hibernate"));
    }

    @Test
    void publishCourse__should_return_ok_when_request_is_valid() throws Exception {
        doNothing().when(courseService).publishCourse(42L,1L);

        mockMvc.perform(post("/course/42/publish")
                        .with(jwt().jwt(jwt -> jwt
                                .claim("scope", "INSTRUCTOR")
                                .subject("1")
                        ))              )
                .andExpect(status().isOk());

        verify(courseService).publishCourse(42L, 1L);
    }
}