package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Student;
import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
    Collection<Student> findByAgeBetween(int min, int max);

    // Новый метод для поиска студентов по факультету
    Collection<Student> findByFaculty_Id(Long facultyId);
}