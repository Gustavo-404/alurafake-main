package br.com.alura.AluraFake.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByCourseIdAndOrder(Long courseId, Integer order);

    boolean existsByCourseIdAndStatement(Long courseId, String statement);

    @Modifying // Informa ao Spring que esta query não é uma consulta, mas sim uma modificação.
    @Query("UPDATE Task t SET t.order = t.order + 1 WHERE t.course.id = :courseId AND t.order >= :order")
    void shiftOrdersForward(@Param("courseId") Long courseId, @Param("order") Integer order);
}