package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;
    private final StudentService studentService;

    public FacultyController(FacultyService facultyService, StudentService studentService) {
        this.facultyService = facultyService;
        this.studentService = studentService;
    }

    // ============== CRUD ОПЕРАЦИИ ==============

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        Faculty createdFaculty = facultyService.createFaculty(faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.editFaculty(faculty);
        if (foundFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    // ============== ФИЛЬТРАЦИЯ ==============

    @GetMapping("/color/{color}")
    public ResponseEntity<Collection<Faculty>> getFacultiesByColor(@PathVariable String color) {
        Collection<Faculty> faculties = facultyService.findByColor(color);
        if (faculties.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Faculty>> getFacultiesByNameOrColor(
            @RequestParam String query) {
        Collection<Faculty> faculties = facultyService.findByNameOrColor(query);
        if (faculties.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties);
    }

    // ============== СВЯЗЬ СО СТУДЕНТАМИ ==============

    @GetMapping("/{id}/students")
    public ResponseEntity<Collection<Student>> getFacultyStudents(@PathVariable Long id) {
        Collection<Student> students = facultyService.getFacultyStudents(id);
        if (students == null || students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{facultyId}/student/{studentId}")
    public ResponseEntity<Student> assignFacultyToStudent(
            @PathVariable Long facultyId,
            @PathVariable Long studentId) {
        Student student = studentService.findStudent(studentId);
        Faculty faculty = facultyService.findFaculty(facultyId);

        if (student == null || faculty == null) {
            return ResponseEntity.notFound().build();
        }

        student.setFaculty(faculty);
        studentService.editStudent(student);

        return ResponseEntity.ok(student);
    }

    @GetMapping("/by-student")
    public ResponseEntity<Faculty> getFacultyByStudentName(@RequestParam String studentName) {
        Faculty faculty = facultyService.getFacultyByStudentName(studentName);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/by-student-id/{studentId}")
    public ResponseEntity<Faculty> getFacultyByStudentId(@PathVariable Long studentId) {
        Student student = studentService.findStudent(studentId);
        if (student == null || student.getFaculty() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student.getFaculty());
    }
}