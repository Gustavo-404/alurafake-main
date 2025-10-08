package br.com.alura.AluraFake.infra;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.task.model.MultipleChoiceTask;
import br.com.alura.AluraFake.task.model.OpenTextTask;
import br.com.alura.AluraFake.task.model.Option;
import br.com.alura.AluraFake.task.model.SingleChoiceTask;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository, TaskRepository taskRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!"dev".equals(activeProfile) || userRepository.count() > 0) {
            return;
        }

        System.out.println("Populando banco de dados de desenvolvimento...");

        User ana = new User("Ana", "ana@alura.com.br", Role.INSTRUCTOR, bCryptPasswordEncoder.encode("123456"));
        User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR, bCryptPasswordEncoder.encode("123456"));
        User thiago = new User("Thiago", "thiago@alura.com.br", Role.INSTRUCTOR, bCryptPasswordEncoder.encode("123456"));
        User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT, bCryptPasswordEncoder.encode("123456"));
        User maria = new User("Maria", "maria@alura.com.br", Role.STUDENT, bCryptPasswordEncoder.encode("123456"));
        userRepository.saveAll(Arrays.asList(ana, paulo, thiago, caio, maria));

        Course javaCourse = new Course("Java Completo", "Aprenda Java do zero ao avançado", ana);
        javaCourse.publish();
        courseRepository.save(javaCourse);
        taskRepository.saveAll(List.of(
                new OpenTextTask(javaCourse, "O que é JVM?", 1),
                new SingleChoiceTask(javaCourse, "Qual a palavra-chave para herança?", 2, List.of(
                        new Option("extends", true), new Option("implements", false)
                )),
                new MultipleChoiceTask(javaCourse, "Quais são pilares da POO?", 3, List.of(
                        new Option("Herança", true), new Option("Encapsulamento", true), new Option("SQL", false)
                ))
        ));

        Course springCourse = new Course("Spring Boot Avançado", "Crie APIs REST com Spring", ana);
        courseRepository.save(springCourse);
        taskRepository.saveAll(List.of(
                new OpenTextTask(springCourse, "O que é Injeção de Dependência?", 1),
                new SingleChoiceTask(springCourse, "Qual anotação principal do Spring Boot?", 2, List.of(
                        new Option("@SpringBootApplication", true), new Option("@Service", false), new Option("@Entity", false)
                )),
                new MultipleChoiceTask(springCourse, "Quais módulos do Spring você conhece?", 3, List.of(
                        new Option("Spring Data JPA", true), new Option("Spring Security", true), new Option("Hibernate", false)
                ))
        ));

        Course sqlCourse = new Course("SQL e Banco de Dados", "Domine o SQL", paulo);
        courseRepository.save(sqlCourse);
        taskRepository.saveAll(List.of(
                new OpenTextTask(sqlCourse, "O que é uma Chave Primária?", 1),
                new SingleChoiceTask(sqlCourse, "Qual comando deleta dados de uma tabela?", 2, List.of(
                        new Option("DELETE", true), new Option("DROP", false), new Option("REMOVE", false)
                ))
        ));

        System.out.println("Banco de dados populado com sucesso!");

    }
}