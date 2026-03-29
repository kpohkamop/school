package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
@Transactional
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    // ==================== CRUD ОПЕРАЦИИ ====================

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty with name: {}", faculty.getName());
        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.debug("Faculty created with id: {}", savedFaculty.getId());
        return savedFaculty;
    }

    @Transactional(readOnly = true)
    public Faculty findFaculty(long id) {
        logger.info("Was invoked method for find faculty by id: {}", id);
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method for edit faculty with id: {}", faculty.getId());
        if (facultyRepository.existsById(faculty.getId())) {
            Faculty updatedFaculty = facultyRepository.save(faculty);
            logger.debug("Faculty updated with id: {}", updatedFaculty.getId());
            return updatedFaculty;
        }
        logger.warn("Attempt to edit non-existent faculty with id: {}", faculty.getId());
        return null;
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
            logger.debug("Faculty deleted with id: {}", id);
        } else {
            logger.warn("Attempt to delete non-existent faculty with id: {}", id);
        }
    }

    @Transactional(readOnly = true)
    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        Collection<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    // ==================== ФИЛЬТРАЦИЯ ====================

    @Transactional(readOnly = true)
    public Collection<Faculty> findByColor(String color) {
        logger.info("Was invoked method for find faculties by color: {}", color);
        Collection<Faculty> faculties = facultyRepository.findByColor(color);
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    @Transactional(readOnly = true)
    public Collection<Faculty> findByNameOrColor(String query) {
        logger.info("Was invoked method for find faculties by name or color: {}", query);
        Collection<Faculty> faculties = facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(query, query);
        logger.debug("Found {} faculties matching query '{}'", faculties.size(), query);
        return faculties;
    }

    // ==================== СВЯЗИ СО СТУДЕНТАМИ ====================

    @Transactional(readOnly = true)
    public Collection<Student> getFacultyStudents(Long facultyId) {
        logger.info("Was invoked method for get students of faculty with id: {}", facultyId);
        Faculty faculty = findFaculty(facultyId);
        if (faculty == null) {
            logger.error("Faculty not found with id: {}", facultyId);
            return null;
        }
        Collection<Student> students = faculty.getStudents();
        logger.debug("Found {} students for faculty with id {}", students.size(), facultyId);
        return students;
    }

    @Transactional(readOnly = true)
    public Faculty getFacultyByStudentName(String studentName) {
        logger.info("Was invoked method for get faculty by student name: {}", studentName);
        return facultyRepository.findByStudents_Name(studentName).orElse(null);
    }
}