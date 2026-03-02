package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    // ==================== CRUD ОПЕРАЦИИ ====================

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Student findStudent(long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student editStudent(Student student) {
        if (studentRepository.existsById(student.getId())) {
            return studentRepository.save(student);
        }
        return null;
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // ==================== ФИЛЬТРАЦИЯ ====================

    @Transactional(readOnly = true)
    public Collection<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }

    @Transactional(readOnly = true)
    public Collection<Student> findByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    // ==================== СВЯЗИ С ФАКУЛЬТЕТАМИ ====================

    @Transactional(readOnly = true)
    public Faculty getStudentFaculty(Long studentId) {
        Student student = findStudent(studentId);
        return student != null ? student.getFaculty() : null;
    }

    @Transactional(readOnly = true)
    public Collection<Student> getFacultyStudents(Long facultyId) {
        return studentRepository.findByFaculty_Id(facultyId);
    }
}