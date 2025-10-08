package br.com.alura.AluraFake.course.controller;

import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.dto.NewCourseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService){
        this.courseService = courseService;
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse, JwtAuthenticationToken token) {
        courseService.createCourse(newCourse, Long.parseLong(token.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> findAllCourses() {
        return ResponseEntity.ok(courseService.findAllCourses());
    }

    @PostMapping("/course/{id}/publish")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity publishCourse(@PathVariable("id") Long id, JwtAuthenticationToken token) {
        courseService.publishCourse(id, Long.valueOf(token.getName()));
        return ResponseEntity.ok().build();
    }

}
