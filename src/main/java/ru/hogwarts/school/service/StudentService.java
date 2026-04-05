package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    // ==================== CRUD ОПЕРАЦИИ ====================

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student with name: {}", student.getName());
        Student savedStudent = studentRepository.save(student);
        logger.debug("Student created with id: {}", savedStudent.getId());
        return savedStudent;
    }

    @Transactional(readOnly = true)
    public Student findStudent(long id) {
        logger.info("Was invoked method for find student by id: {}", id);
        return studentRepository.findById(id).orElse(null);
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student with id: {}", student.getId());
        if (studentRepository.existsById(student.getId())) {
            Student updatedStudent = studentRepository.save(student);
            logger.debug("Student updated with id: {}", updatedStudent.getId());
            return updatedStudent;
        }
        logger.warn("Attempt to edit non-existent student with id: {}", student.getId());
        return null;
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student with id: {}", id);
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            logger.debug("Student deleted with id: {}", id);
        } else {
            logger.warn("Attempt to delete non-existent student with id: {}", id);
        }
    }

    @Transactional(readOnly = true)
    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        Collection<Student> students = studentRepository.findAll();
        logger.debug("Found {} students", students.size());
        return students;
    }

    // ==================== ФИЛЬТРАЦИЯ ====================

    @Transactional(readOnly = true)
    public Collection<Student> findByAge(int age) {
        logger.info("Was invoked method for find students by age: {}", age);
        Collection<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age {}", students.size(), age);
        return students;
    }

    @Transactional(readOnly = true)
    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find students by age between {} and {}", min, max);
        Collection<Student> students = studentRepository.findByAgeBetween(min, max);
        logger.debug("Found {} students in age range {} - {}", students.size(), min, max);
        return students;
    }

    // ==================== SQL ЗАПРОСЫ ====================

    @Transactional(readOnly = true)
    public int getCountOfStudents() {
        logger.info("Was invoked method for get count of students");
        int count = studentRepository.getCountOfStudents();
        logger.debug("Total students count: {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    public double getAverageAge() {
        logger.info("Was invoked method for get average age of students");
        double averageAge = studentRepository.getAverageAge();
        logger.debug("Average age of students: {}", averageAge);
        return averageAge;
    }

    @Transactional(readOnly = true)
    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        List<Student> students = studentRepository.getLastFiveStudents();
        logger.debug("Found {} last students", students.size());
        return students;
    }

    // ==================== НОВЫЕ МЕТОДЫ ДЛЯ STREAM API ====================

    /**
     * Шаг 1: Получить всех студентов, чье имя начинается с буквы А
     * Отсортированные в алфавитном порядке имена в верхнем регистре
     */
    @Transactional(readOnly = true)
    public List<String> getStudentNamesStartingWithA() {
        logger.info("Was invoked method for get student names starting with A");

        List<String> names = studentRepository.findAll().stream()
                .map(Student::getName)                          // берем имена
                .filter(name -> name != null && !name.isEmpty()) // фильтруем пустые
                .map(String::toUpperCase)                       // переводим в верхний регистр
                .filter(name -> name.startsWith("А"))           // оставляем только начинающиеся с А
                .sorted()                                        // сортируем в алфавитном порядке
                .collect(Collectors.toList());

        logger.debug("Found {} students with names starting with A", names.size());
        return names;
    }

    /**
     * Шаг 2: Получить средний возраст всех студентов
     */
    @Transactional(readOnly = true)
    public double getAverageAgeOfAllStudents() {
        logger.info("Was invoked method for get average age of all students");

        double averageAge = studentRepository.findAll().stream()
                .mapToInt(Student::getAge)      // преобразуем в IntStream
                .average()                       // вычисляем среднее
                .orElse(0.0);                    // если студентов нет, возвращаем 0.0

        logger.debug("Average age of all students: {}", averageAge);
        return averageAge;
    }

    // ==================== СВЯЗИ С ФАКУЛЬТЕТАМИ ====================

    @Transactional(readOnly = true)
    public Faculty getStudentFaculty(Long studentId) {
        logger.info("Was invoked method for get faculty of student with id: {}", studentId);
        Student student = findStudent(studentId);
        if (student == null) {
            logger.error("Student not found with id: {}", studentId);
            return null;
        }
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            logger.warn("Student with id {} has no faculty", studentId);
        } else {
            logger.debug("Student with id {} belongs to faculty: {}", studentId, faculty.getName());
        }
        return faculty;
    }

    @Transactional(readOnly = true)
    public Collection<Student> getFacultyStudents(Long facultyId) {
        logger.info("Was invoked method for get students of faculty with id: {}", facultyId);
        Collection<Student> students = studentRepository.findByFaculty_Id(facultyId);
        logger.debug("Found {} students for faculty with id {}", students.size(), facultyId);
        return students;
    }
}
