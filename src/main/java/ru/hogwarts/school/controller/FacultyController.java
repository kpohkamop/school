package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);

    private final FacultyService facultyService;
    private final StudentService studentService;

    public FacultyController(FacultyService facultyService, StudentService studentService) {
        this.facultyService = facultyService;
        this.studentService = studentService;
    }

    // ============== CRUD ОПЕРАЦИИ ==============

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable Long id) {
        logger.info("Was invoked method for get faculty by id: {}", id);
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            logger.warn("Faculty not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        logger.info("Was invoked method for create faculty with name: {}", faculty.getName());
        Faculty createdFaculty = facultyService.createFaculty(faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        logger.info("Was invoked method for edit faculty with id: {}", faculty.getId());
        Faculty foundFaculty = facultyService.editFaculty(faculty);
        if (foundFaculty == null) {
            logger.warn("Attempt to edit non-existent faculty with id: {}", faculty.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    // ============== ФИЛЬТРАЦИЯ ==============

    @GetMapping("/color/{color}")
    public ResponseEntity<Collection<Faculty>> getFacultiesByColor(@PathVariable String color) {
        logger.info("Was invoked method for find faculties by color: {}", color);
        Collection<Faculty> faculties = facultyService.findByColor(color);
        if (faculties.isEmpty()) {
            logger.warn("No faculties found with color: {}", color);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Faculty>> getFacultiesByNameOrColor(
            @RequestParam String query) {
        logger.info("Was invoked method for find faculties by name or color: {}", query);
        Collection<Faculty> faculties = facultyService.findByNameOrColor(query);
        if (faculties.isEmpty()) {
            logger.warn("No faculties found matching query: {}", query);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties);
    }

    // ============== НОВЫЙ ЭНДПОИНТ ДЛЯ STREAM API ==============

    @GetMapping("/longest-name")
    public ResponseEntity<String> getLongestFacultyName() {
        logger.info("Was invoked method for get longest faculty name");
        String longestName = facultyService.getLongestFacultyName();
        if (longestName.isEmpty()) {
            logger.warn("No faculties found");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(longestName);
    }

    // ============== СВЯЗИ СО СТУДЕНТАМИ ==============

    @GetMapping("/{id}/students")
    public ResponseEntity<Collection<Student>> getFacultyStudents(@PathVariable Long id) {
        logger.info("Was invoked method for get students of faculty with id: {}", id);
        Collection<Student> students = facultyService.getFacultyStudents(id);
        if (students == null || students.isEmpty()) {
            logger.warn("No students found for faculty with id: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{facultyId}/student/{studentId}")
    public ResponseEntity<Student> assignFacultyToStudent(
            @PathVariable Long facultyId,
            @PathVariable Long studentId) {
        logger.info("Was invoked method for assign faculty {} to student {}", facultyId, studentId);
        Student student = studentService.findStudent(studentId);
        Faculty faculty = facultyService.findFaculty(facultyId);

        if (student == null || faculty == null) {
            logger.warn("Faculty {} or Student {} not found", facultyId, studentId);
            return ResponseEntity.notFound().build();
        }

        student.setFaculty(faculty);
        studentService.editStudent(student);
        logger.info("Faculty {} assigned to student {}", facultyId, studentId);

        return ResponseEntity.ok(student);
    }

    @GetMapping("/by-student")
    public ResponseEntity<Faculty> getFacultyByStudentName(@RequestParam String studentName) {
        logger.info("Was invoked method for get faculty by student name: {}", studentName);
        Faculty faculty = facultyService.getFacultyByStudentName(studentName);
        if (faculty == null) {
            logger.warn("No faculty found for student: {}", studentName);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/by-student-id/{studentId}")
    public ResponseEntity<Faculty> getFacultyByStudentId(@PathVariable Long studentId) {
        logger.info("Was invoked method for get faculty by student id: {}", studentId);
        Student student = studentService.findStudent(studentId);
        if (student == null || student.getFaculty() == null) {
            logger.warn("No faculty found for student id: {}", studentId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student.getFaculty());
    }
}
