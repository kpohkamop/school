package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        if (facultyRepository.existsById(faculty.getId())) {
            return facultyRepository.save(faculty);
        }
        return null;
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> findByNameOrColor(String query) {
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(query, query);
    }

    // Новый метод: получить студентов факультета
    public Collection<Student> getFacultyStudents(Long facultyId) {
        Faculty faculty = findFaculty(facultyId);
        return faculty != null ? faculty.getStudents() : null;
    }

    // Новый метод: получить факультет по имени студента
    public Faculty getFacultyByStudentName(String studentName) {
        return facultyRepository.findByStudents_Name(studentName).orElse(null);
    }
}