package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private Long lastId = 0L;

    public Student createStudent(Student student) {
        student.setId(++lastId);
        students.put(lastId, student);
        return student;
    }

    public Student findStudent(Long id) {
        return students.get(id);
    }

    public Student editStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    public Student deleteStudent(Long id) {
        return students.remove(id);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }

    public Collection<Student> findByAge(int age) {
        return students.values().stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }
}