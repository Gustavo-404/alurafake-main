package br.com.alura.AluraFake.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    long countByCourseId(Long courseId);

    boolean existsByCourseIdAndOrder(Long courseId, Integer order);

    boolean existsByCourseIdAndStatement(Long courseId, String statement);

    List<Task> findAllByCourseIdOrderByOrderAsc(Long courseId);

    List<Task> findAllByCourseIdAndOrderGreaterThanEqualOrderByOrderDesc(Long courseId, Integer order);
}