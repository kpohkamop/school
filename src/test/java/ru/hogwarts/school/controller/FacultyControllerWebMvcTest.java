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

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    @MockBean  // ВАЖНО: добавляем мок для StudentService, так как FacultyController требует его в конструкторе
    private StudentService studentService;

    @Test
    void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");
        Faculty createdFaculty = new Faculty(1L, "Гриффиндор", "Красный");

        Mockito.when(facultyService.createFaculty(any(Faculty.class))).thenReturn(createdFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    @Test
    void testGetFacultyById() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        Mockito.when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    @Test
    void testGetFacultyByIdNotFound() throws Exception {
        Mockito.when(facultyService.findFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEditFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Золотой");

        Mockito.when(facultyService.editFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Золотой"));
    }

    @Test
    void testEditFacultyNotFound() throws Exception {
        Faculty faculty = new Faculty(999L, "Неизвестный", "Цвет");

        Mockito.when(facultyService.editFaculty(any(Faculty.class))).thenReturn(null);

        mockMvc.perform(put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteFaculty() throws Exception {
        Mockito.doNothing().when(facultyService).deleteFaculty(1L);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFaculties() throws Exception {
        Collection<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Гриффиндор", "Красный"),
                new Faculty(2L, "Слизерин", "Зеленый")
        );

        Mockito.when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[1].name").value("Слизерин"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetFacultiesByColor() throws Exception {
        Collection<Faculty> faculties = Collections.singletonList(
                new Faculty(1L, "Гриффиндор", "Красный")
        );

        Mockito.when(facultyService.findByColor("Красный")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/color/Красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[0].color").value("Красный"));
    }

    @Test
    void testGetFacultiesByColorNotFound() throws Exception {
        Mockito.when(facultyService.findByColor("Несуществующий")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/faculty/color/Несуществующий"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchFacultiesByNameOrColor() throws Exception {
        Collection<Faculty> faculties = Collections.singletonList(
                new Faculty(1L, "Гриффиндор", "Красный")
        );

        Mockito.when(facultyService.findByNameOrColor("Гриф")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/search")
                        .param("query", "Гриф"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    void testSearchFacultiesNotFound() throws Exception {
        Mockito.when(facultyService.findByNameOrColor("Неизвестно")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/faculty/search")
                        .param("query", "Неизвестно"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFacultyStudents() throws Exception {
        Collection<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 17),
                new Student(2L, "Гермиона Грейнджер", 17)
        );

        Mockito.when(facultyService.getFacultyStudents(1L)).thenReturn(students);

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Гермиона Грейнджер"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetFacultyStudentsNotFound() throws Exception {
        Mockito.when(facultyService.getFacultyStudents(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/999/students"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFacultyStudentsEmpty() throws Exception {
        Mockito.when(facultyService.getFacultyStudents(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFacultyByStudentName() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        Mockito.when(facultyService.getFacultyByStudentName("Гарри Поттер")).thenReturn(faculty);

        mockMvc.perform(get("/faculty/by-student")
                        .param("studentName", "Гарри Поттер"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    @Test
    void testGetFacultyByStudentNameNotFound() throws Exception {
        Mockito.when(facultyService.getFacultyByStudentName("Неизвестный")).thenReturn(null);

        mockMvc.perform(get("/faculty/by-student")
                        .param("studentName", "Неизвестный"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFacultyByStudentId() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        Student student = new Student(1L, "Гарри Поттер", 17);
        student.setFaculty(faculty);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/faculty/by-student-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }

    @Test
    void testGetFacultyByStudentIdNotFound() throws Exception {
        Mockito.when(studentService.findStudent(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/by-student-id/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFacultyByStudentIdNoFaculty() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 17);
        student.setFaculty(null);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/faculty/by-student-id/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignFacultyToStudent() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 17);
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        student.setFaculty(faculty);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);
        Mockito.when(facultyService.findFaculty(1L)).thenReturn(faculty);
        Mockito.when(studentService.editStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/faculty/1/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.faculty.name").value("Гриффиндор"));
    }

    @Test
    void testAssignFacultyToStudentNotFound() throws Exception {
        Mockito.when(studentService.findStudent(999L)).thenReturn(null);

        mockMvc.perform(put("/faculty/1/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignFacultyToStudentFacultyNotFound() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 17);

        Mockito.when(studentService.findStudent(1L)).thenReturn(student);
        Mockito.when(facultyService.findFaculty(999L)).thenReturn(null);

        mockMvc.perform(put("/faculty/999/student/1"))
                .andExpect(status().isNotFound());
    }
}