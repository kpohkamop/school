package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final FacultyService facultyService;

    public StudentController(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    // ============== CRUD ОПЕРАЦИИ ==============

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // ============== ФИЛЬТРАЦИЯ ==============

    @GetMapping("/age/{age}")
    public ResponseEntity<Collection<Student>> getStudentsByAge(@PathVariable int age) {
        Collection<Student> students = studentService.findByAge(age);
        if (students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }

    @GetMapping("/age-between")
    public ResponseEntity<Collection<Student>> getStudentsByAgeBetween(
            @RequestParam int min,
            @RequestParam int max) {
        Collection<Student> students = studentService.findByAgeBetween(min, max);
        if (students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }

    // ============== СВЯЗЬ С ФАКУЛЬТЕТАМИ ==============

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        Faculty faculty = studentService.getStudentFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping("/{studentId}/faculty/{facultyId}")
    public ResponseEntity<Student> assignStudentToFaculty(
            @PathVariable Long studentId,
            @PathVariable Long facultyId) {
        Student student = studentService.findStudent(studentId);
        Faculty faculty = facultyService.findFaculty(facultyId);

        if (student == null || faculty == null) {
            return ResponseEntity.notFound().build();
        }

        student.setFaculty(faculty);
        studentService.editStudent(student);

        return ResponseEntity.ok(student);
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<Collection<Student>> getStudentsByFaculty(@PathVariable Long facultyId) {
        Collection<Student> students = studentService.getFacultyStudents(facultyId);
        if (students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }
}