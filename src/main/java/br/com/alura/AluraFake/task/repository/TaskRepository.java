package br.com.alura.AluraFake.task.repository;

import br.com.alura.AluraFake.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    long countByCourseId(Long courseId);

    boolean existsByCourseIdAndOrder(Long courseId, Integer order);

    boolean existsByCourseIdAndStatement(Long courseId, String statement);

    List<Task> findAllByCourseIdOrderByOrderAsc(Long courseId);

    List<Task> findAllByCourseIdAndOrderGreaterThanEqualOrderByOrderDesc(Long courseId, Integer order);
}