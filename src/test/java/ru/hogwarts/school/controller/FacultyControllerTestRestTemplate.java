package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FacultyControllerTestRestTemplate {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String facultyUrl;
    private String studentUrl;

    @BeforeEach
    void setUp() {
        facultyUrl = "http://localhost:" + port + "/faculty";
        studentUrl = "http://localhost:" + port + "/student";
    }

    // ==================== CRUD ТЕСТЫ ====================

    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty(null, "Тестовый Факультет", "Фиолетовый");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Факультет");
        assertThat(response.getBody().getColor()).isEqualTo("Фиолетовый");
    }

    @Test
    void testGetFacultyById() {
        Faculty faculty = new Faculty(null, "Тестовый Факультет", "Фиолетовый");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long id = createResponse.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(facultyUrl + "/" + id, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Факультет");
        assertThat(response.getBody().getColor()).isEqualTo("Фиолетовый");
    }

    @Test
    void testGetFacultyByIdNotFound() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(facultyUrl + "/999999", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testEditFaculty() {
        Faculty faculty = new Faculty(null, "Тестовый Факультет", "Фиолетовый");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long id = createResponse.getBody().getId();

        Faculty updatedFaculty = new Faculty(id, "Обновленный Факультет", "Золотой");
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(updatedFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                facultyUrl, HttpMethod.PUT, requestEntity, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isEqualTo("Обновленный Факультет");
        assertThat(response.getBody().getColor()).isEqualTo("Золотой");
    }

    @Test
    void testEditFacultyNotFound() {
        Faculty updatedFaculty = new Faculty(999999L, "Несуществующий", "Цвет");
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(updatedFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                facultyUrl, HttpMethod.PUT, requestEntity, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDeleteFaculty() {
        Faculty faculty = new Faculty(null, "Тестовый Факультет", "Фиолетовый");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long id = createResponse.getBody().getId();

        restTemplate.delete(facultyUrl + "/" + id);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(facultyUrl + "/" + id, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetAllFaculties() {
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Факультет 1", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Факультет 2", "Синий"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Факультет 3", "Зеленый"), Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(facultyUrl, Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(3);
    }

    // ==================== ТЕСТЫ ФИЛЬТРАЦИИ ПО ЦВЕТУ ====================

    @Test
    void testGetFacultiesByColor() {
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Гриффиндор", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Слизерин", "Зеленый"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Когтевран", "Синий"), Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(facultyUrl + "/color/Красный", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
        Arrays.stream(response.getBody()).forEach(f -> assertThat(f.getColor()).isEqualTo("Красный"));
    }

    @Test
    void testGetFacultiesByColorMultiple() {
        // Создаем несколько факультетов с одинаковым цветом
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Красный Факультет 1", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Красный Факультет 2", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Красный Факультет 3", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Красный Факультет 4", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Красный Факультет 5", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Синий Факультет", "Синий"), Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(facultyUrl + "/color/Красный", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(5);

        // Проверяем, что все найденные факультеты имеют красный цвет
        Arrays.stream(response.getBody()).forEach(f -> {
            assertThat(f.getColor()).isEqualTo("Красный");
        });

        // Проверяем, что все 5 факультетов имеют разные имена
        long distinctNames = Arrays.stream(response.getBody())
                .map(Faculty::getName)
                .distinct()
                .count();
        assertThat(distinctNames).isEqualTo(5);
    }

    @Test
    void testGetFacultiesByColorNotFound() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(facultyUrl + "/color/НесуществующийЦвет", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetFacultiesByColorCaseInsensitive() {
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Гриффиндор", "Красный"), Faculty.class);

        // Тестируем с разным регистром
        ResponseEntity<Faculty[]> responseUpper = restTemplate.getForEntity(facultyUrl + "/color/КРАСНЫЙ", Faculty[].class);
        ResponseEntity<Faculty[]> responseLower = restTemplate.getForEntity(facultyUrl + "/color/красный", Faculty[].class);
        ResponseEntity<Faculty[]> responseMixed = restTemplate.getForEntity(facultyUrl + "/color/КрАсНыЙ", Faculty[].class);
        ResponseEntity<Faculty[]> responseNormal = restTemplate.getForEntity(facultyUrl + "/color/Красный", Faculty[].class);

        assertThat(responseUpper.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseLower.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseMixed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseNormal.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseUpper.getBody()).isNotNull();
        assertThat(responseLower.getBody()).isNotNull();
        assertThat(responseMixed.getBody()).isNotNull();
        assertThat(responseNormal.getBody()).isNotNull();

        assertThat(responseUpper.getBody().length).isEqualTo(1);
        assertThat(responseLower.getBody().length).isEqualTo(1);
        assertThat(responseMixed.getBody().length).isEqualTo(1);
        assertThat(responseNormal.getBody().length).isEqualTo(1);

        // Проверяем, что во всех случаях найден тот же факультет
        assertThat(responseUpper.getBody()[0].getName()).isEqualTo("Гриффиндор");
        assertThat(responseLower.getBody()[0].getName()).isEqualTo("Гриффиндор");
        assertThat(responseMixed.getBody()[0].getName()).isEqualTo("Гриффиндор");
    }

    @Test
    void testGetFacultiesByColorWithMultipleAndCaseInsensitive() {
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Красный 1", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Красный 2", "Красный"), Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(facultyUrl + "/color/КРАСНЫЙ", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
    }

    // ==================== ТЕСТЫ ПОИСКА ====================

    @Test
    void testSearchFacultiesByNameOrColor() {
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Гриффиндор", "Красный"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Слизерин", "Зеленый"), Faculty.class);
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Когтевран", "Синий"), Faculty.class);

        // Поиск по имени
        ResponseEntity<Faculty[]> responseByName = restTemplate.getForEntity(
                facultyUrl + "/search?query=Гриф", Faculty[].class);

        assertThat(responseByName.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseByName.getBody()).isNotNull();
        assertThat(responseByName.getBody().length).isGreaterThanOrEqualTo(1);
        assertThat(responseByName.getBody()[0].getName()).contains("Гриф");

        // Поиск по цвету
        ResponseEntity<Faculty[]> responseByColor = restTemplate.getForEntity(
                facultyUrl + "/search?query=Синий", Faculty[].class);

        assertThat(responseByColor.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseByColor.getBody()).isNotNull();
        assertThat(responseByColor.getBody().length).isGreaterThanOrEqualTo(1);
        assertThat(responseByColor.getBody()[0].getColor()).isEqualTo("Синий");
    }

    @Test
    void testSearchFacultiesNotFound() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                facultyUrl + "/search?query=Несуществующий", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testSearchFacultiesCaseInsensitive() {
        restTemplate.postForEntity(facultyUrl, new Faculty(null, "Гриффиндор", "Красный"), Faculty.class);

        // Поиск в разном регистре
        ResponseEntity<Faculty[]> responseUpper = restTemplate.getForEntity(
                facultyUrl + "/search?query=ГРИФ", Faculty[].class);
        ResponseEntity<Faculty[]> responseLower = restTemplate.getForEntity(
                facultyUrl + "/search?query=гриф", Faculty[].class);
        ResponseEntity<Faculty[]> responseMixed = restTemplate.getForEntity(
                facultyUrl + "/search?query=ГрИф", Faculty[].class);

        assertThat(responseUpper.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseLower.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseMixed.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseUpper.getBody().length).isEqualTo(1);
        assertThat(responseLower.getBody().length).isEqualTo(1);
        assertThat(responseMixed.getBody().length).isEqualTo(1);
    }

    // ==================== ТЕСТЫ СВЯЗЕЙ ====================

    @Test
    void testGetFacultyStudents() {
        // Создаем факультет
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаем студентов
        Student student1 = new Student(null, "Гарри Поттер", 17);
        Student student2 = new Student(null, "Гермиона Грейнджер", 17);
        Student student3 = new Student(null, "Рон Уизли", 17);

        ResponseEntity<Student> studentResponse1 = restTemplate.postForEntity(studentUrl, student1, Student.class);
        ResponseEntity<Student> studentResponse2 = restTemplate.postForEntity(studentUrl, student2, Student.class);
        ResponseEntity<Student> studentResponse3 = restTemplate.postForEntity(studentUrl, student3, Student.class);

        Long studentId1 = studentResponse1.getBody().getId();
        Long studentId2 = studentResponse2.getBody().getId();
        Long studentId3 = studentResponse3.getBody().getId();

        // Назначаем студентов на факультет
        restTemplate.put(studentUrl + "/" + studentId1 + "/faculty/" + facultyId, null);
        restTemplate.put(studentUrl + "/" + studentId2 + "/faculty/" + facultyId, null);
        restTemplate.put(studentUrl + "/" + studentId3 + "/faculty/" + facultyId, null);

        // Получаем студентов факультета
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                facultyUrl + "/" + facultyId + "/students", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(3);

        // Проверяем имена студентов
        boolean hasHarry = false;
        boolean hasHermione = false;
        boolean hasRon = false;

        for (Student student : response.getBody()) {
            if (student.getName().equals("Гарри Поттер")) hasHarry = true;
            if (student.getName().equals("Гермиона Грейнджер")) hasHermione = true;
            if (student.getName().equals("Рон Уизли")) hasRon = true;
        }

        assertThat(hasHarry).isTrue();
        assertThat(hasHermione).isTrue();
        assertThat(hasRon).isTrue();
    }

    @Test
    void testGetFacultyStudentsNotFound() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(facultyUrl + "/999999/students", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetFacultyStudentsEmpty() {
        // Создаем факультет без студентов
        Faculty faculty = new Faculty(null, "Пустой Факультет", "Серый");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                facultyUrl + "/" + facultyId + "/students", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetFacultyByStudentName() {
        // Создаем факультет
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаем студента
        Student student = new Student(null, "Гарри Поттер", 17);
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(studentUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        // Назначаем студента на факультет
        restTemplate.put(studentUrl + "/" + studentId + "/faculty/" + facultyId, null);

        // Получаем факультет по имени студента
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                facultyUrl + "/by-student?studentName=Гарри Поттер", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
        assertThat(response.getBody().getName()).isEqualTo("Гриффиндор");
        assertThat(response.getBody().getColor()).isEqualTo("Красный");
    }

    @Test
    void testGetFacultyByStudentNameCaseInsensitive() {
        // Создаем факультет
        Faculty faculty = new Faculty(null, "Слизерин", "Зеленый");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаем студента
        Student student = new Student(null, "Драко Малфой", 17);
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(studentUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        restTemplate.put(studentUrl + "/" + studentId + "/faculty/" + facultyId, null);

        // Тестируем с разным регистром
        ResponseEntity<Faculty> responseUpper = restTemplate.getForEntity(
                facultyUrl + "/by-student?studentName=ДРАКО МАЛФОЙ", Faculty.class);
        ResponseEntity<Faculty> responseLower = restTemplate.getForEntity(
                facultyUrl + "/by-student?studentName=драко малфой", Faculty.class);
        ResponseEntity<Faculty> responseMixed = restTemplate.getForEntity(
                facultyUrl + "/by-student?studentName=ДрАкО МалФой", Faculty.class);

        assertThat(responseUpper.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseLower.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseMixed.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseUpper.getBody().getId()).isEqualTo(facultyId);
        assertThat(responseLower.getBody().getId()).isEqualTo(facultyId);
        assertThat(responseMixed.getBody().getId()).isEqualTo(facultyId);
    }

    @Test
    void testGetFacultyByStudentNameNotFound() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                facultyUrl + "/by-student?studentName=Неизвестный", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetFacultyByStudentNameWithSpecialCharacters() {
        // Создаем факультет
        Faculty faculty = new Faculty(null, "Слизерин", "Зеленый");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаем студента со специальными символами в имени
        Student student = new Student(null, "Драко Малфой-младший", 17);
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(studentUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        restTemplate.put(studentUrl + "/" + studentId + "/faculty/" + facultyId, null);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                facultyUrl + "/by-student?studentName=Драко Малфой-младший", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
    }

    @Test
    void testGetFacultyByStudentId() {
        // Создаем факультет
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаем студента
        Student student = new Student(null, "Гарри Поттер", 17);
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(studentUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        // Назначаем студента на факультет
        restTemplate.put(studentUrl + "/" + studentId + "/faculty/" + facultyId, null);

        // Получаем факультет по ID студента
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                facultyUrl + "/by-student-id/" + studentId, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
    }

    @Test
    void testGetFacultyByStudentIdNotFound() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(facultyUrl + "/by-student-id/999999", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetFacultyByStudentIdNoFaculty() {
        // Создаем студента без факультета
        Student student = new Student(null, "Студент без факультета", 20);
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(studentUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                facultyUrl + "/by-student-id/" + studentId, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testAssignFacultyToStudent() {
        // Создаем факультет
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        // Создаем студента
        Student student = new Student(null, "Гарри Поттер", 17);
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(studentUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        // Назначаем факультет студенту (через FacultyController)
        restTemplate.put(facultyUrl + "/" + facultyId + "/student/" + studentId, null);

        // Проверяем, что у студента есть факультет
        ResponseEntity<Student> checkResponse = restTemplate.getForEntity(studentUrl + "/" + studentId, Student.class);

        assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(checkResponse.getBody().getFaculty()).isNotNull();
        assertThat(checkResponse.getBody().getFaculty().getId()).isEqualTo(facultyId);
        assertThat(checkResponse.getBody().getFaculty().getName()).isEqualTo("Гриффиндор");
    }

    @Test
    void testAssignFacultyToStudentNotFound() {
        ResponseEntity<Student> response = restTemplate.exchange(
                facultyUrl + "/999999/student/999999",
                HttpMethod.PUT,
                null,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testAssignFacultyToStudentStudentNotFound() {
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
        Long facultyId = facultyResponse.getBody().getId();

        ResponseEntity<Student> response = restTemplate.exchange(
                facultyUrl + "/" + facultyId + "/student/999999",
                HttpMethod.PUT,
                null,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testAssignFacultyToStudentFacultyNotFound() {
        Student student = new Student(null, "Гарри Поттер", 17);
        ResponseEntity<Student> studentResponse = restTemplate.postForEntity(studentUrl, student, Student.class);
        Long studentId = studentResponse.getBody().getId();

        ResponseEntity<Student> response = restTemplate.exchange(
                facultyUrl + "/999999/student/" + studentId,
                HttpMethod.PUT,
                null,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}