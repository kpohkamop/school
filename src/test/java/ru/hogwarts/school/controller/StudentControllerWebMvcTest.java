package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean  // ← ЭТО НУЖНО ДОБАВИТЬ!
    private FacultyService facultyService;

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student(null, "Гарри Поттер", 17);
        Student createdStudent = new Student(1L, "Гарри Поттер", 17);

        Mockito.when(studentService.createStudent(any(Student.class))).thenReturn(createdStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void testGetStudentById() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 17);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void testGetStudentByIdNotFound() throws Exception {
        Mockito.when(studentService.findStudent(999L)).thenReturn(null);

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEditStudent() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 18);

        Mockito.when(studentService.editStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void testEditStudentNotFound() throws Exception {
        Student student = new Student(999L, "Неизвестный", 18);

        Mockito.when(studentService.editStudent(any(Student.class))).thenReturn(null);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteStudent() throws Exception {
        Mockito.doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStudents() throws Exception {
        Collection<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 17),
                new Student(2L, "Гермиона Грейнджер", 17)
        );

        Mockito.when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetStudentsByAge() throws Exception {
        Collection<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 17),
                new Student(2L, "Гермиона Грейнджер", 17)
        );

        Mockito.when(studentService.findByAge(17)).thenReturn(students);

        mockMvc.perform(get("/student/age/17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(17))
                .andExpect(jsonPath("$[1].age").value(17));
    }

    @Test
    void testGetStudentsByAgeNotFound() throws Exception {
        Mockito.when(studentService.findByAge(99)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/student/age/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStudentsByAgeBetween() throws Exception {
        Collection<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 17),
                new Student(2L, "Гермиона Грейнджер", 17),
                new Student(3L, "Рон Уизли", 17)
        );

        Mockito.when(studentService.findByAgeBetween(16, 18)).thenReturn(students);

        mockMvc.perform(get("/student/age-between")
                        .param("min", "16")
                        .param("max", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        Mockito.when(studentService.getStudentFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    @Test
    void testGetStudentFacultyNotFound() throws Exception {
        Mockito.when(studentService.getStudentFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/student/999/faculty"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignStudentToFaculty() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 17);
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        student.setFaculty(faculty);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);
        Mockito.when(facultyService.findFaculty(1L)).thenReturn(faculty);
        Mockito.when(studentService.editStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/student/1/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faculty.name").value("Гриффиндор"));
    }

    @Test
    void testAssignStudentToFacultyNotFound() throws Exception {
        Mockito.when(studentService.findStudent(999L)).thenReturn(null);

        mockMvc.perform(put("/student/999/faculty/1"))
                .andExpect(status().isNotFound());
    }
}