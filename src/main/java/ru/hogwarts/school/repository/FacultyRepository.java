package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Faculty;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findByColor(String color);
    Collection<Faculty> findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(String name, String color);
    Optional<Faculty> findByStudents_Name(String studentName);

    // Метод для загрузки факультета со студентами
    @Query("SELECT f FROM Faculty f LEFT JOIN FETCH f.students WHERE f.id = :id")
    Optional<Faculty> findByIdWithStudents(Long id);
}