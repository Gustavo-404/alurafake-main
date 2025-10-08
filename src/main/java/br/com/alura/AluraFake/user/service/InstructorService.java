package br.com.alura.AluraFake.user.service;

import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.dto.InstructorReportDTO;
import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.util.exception.BusinessRuleException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InstructorService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public InstructorService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public InstructorReportDTO generateReport(Long instructorId, Pageable pageable) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.isInstructor()) {
            throw new BusinessRuleException("Usuário não é um instrutor");
        }

        var coursesPage = courseRepository.getCourseReportByInstructor(instructorId, pageable);
        var totalPublished = courseRepository.countByInstructorIdAndStatus(instructorId, Status.PUBLISHED);

        return new InstructorReportDTO(totalPublished, coursesPage);
    }
}