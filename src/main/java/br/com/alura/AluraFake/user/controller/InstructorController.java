package br.com.alura.AluraFake.user.controller;

import br.com.alura.AluraFake.course.dto.InstructorReportDTO;
import br.com.alura.AluraFake.user.service.InstructorService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/instructor/{id}/courses")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity<InstructorReportDTO> getInstructorReport(
            @PathVariable("id") Long id,
            @PageableDefault(size = 5, sort = "title") Pageable pageable) {

        InstructorReportDTO report = instructorService.generateReport(id, pageable);
        return ResponseEntity.ok(report);
    }
}