package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;
    private final FacultyService facultyService;

    public StudentController(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    // ============== CRUD ОПЕРАЦИИ ==============

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        logger.info("Was invoked method for get student by id: {}", id);
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        logger.info("Was invoked method for create student with name: {}", student.getName());
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        logger.info("Was invoked method for edit student with id: {}", student.getId());
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        logger.info("Was invoked method for delete student with id: {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        logger.info("Was invoked method for get all students");
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // ============== ФИЛЬТРАЦИЯ ==============

    @GetMapping("/age/{age}")
    public ResponseEntity<Collection<Student>> getStudentsByAge(@PathVariable int age) {
        logger.info("Was invoked method for find students by age: {}", age);
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
        logger.info("Was invoked method for find students by age between {} and {}", min, max);
        Collection<Student> students = studentService.findByAgeBetween(min, max);
        if (students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }

    // ============== НОВЫЕ ЭНДПОИНТЫ ДЛЯ STREAM API ==============

    @GetMapping("/names-starting-with-a")
    public ResponseEntity<List<String>> getStudentNamesStartingWithA() {
        logger.info("Was invoked method for get student names starting with A");
        List<String> names = studentService.getStudentNamesStartingWithA();
        if (names.isEmpty()) {
            logger.warn("No students found with names starting with A");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }

    @GetMapping("/average-age-all")
    public ResponseEntity<Double> getAverageAgeOfAllStudents() {
        logger.info("Was invoked method for get average age of all students");
        double averageAge = studentService.getAverageAgeOfAllStudents();
        return ResponseEntity.ok(averageAge);
    }

    // ============== SQL ЗАПРОСЫ ==============

    @GetMapping("/count")
    public ResponseEntity<Integer> getCountOfStudents() {
        logger.info("Was invoked method for get count of students");
        int count = studentService.getCountOfStudents();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageAge() {
        logger.info("Was invoked method for get average age of students");
        double averageAge = studentService.getAverageAge();
        return ResponseEntity.ok(averageAge);
    }

    @GetMapping("/last-five")
    public ResponseEntity<List<Student>> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        List<Student> lastFiveStudents = studentService.getLastFiveStudents();
        if (lastFiveStudents.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lastFiveStudents);
    }

    // ============== СВЯЗЬ С ФАКУЛЬТЕТАМИ ==============

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable Long id) {
        logger.info("Was invoked method for get faculty of student with id: {}", id);
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
        logger.info("Was invoked method for assign student {} to faculty {}", studentId, facultyId);
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
        logger.info("Was invoked method for get students of faculty with id: {}", facultyId);
        Collection<Student> students = studentService.getFacultyStudents(facultyId);
        if (students.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }
}
