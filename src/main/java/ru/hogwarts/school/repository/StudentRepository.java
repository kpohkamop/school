package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;
import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
    Collection<Student> findByAgeBetween(int min, int max);
    Collection<Student> findByFaculty_Id(Long facultyId);

    // 1. Получить количество всех студентов
    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    int getCountOfStudents();

    // 2. Получить средний возраст студентов
    @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
    double getAverageAge();

    // 3. Получить пять последних студентов (по убыванию ID)
    @Query(value = "SELECT * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> getLastFiveStudents();
}