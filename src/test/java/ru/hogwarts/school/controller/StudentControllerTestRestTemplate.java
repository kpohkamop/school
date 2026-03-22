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
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StudentControllerTestRestTemplate {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String facultyUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
        facultyUrl = "http://localhost:" + port + "/faculty";
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private ResponseEntity<Student> createTestStudent(String name, int age) {
        Student student = new Student(null, name, age);
        return restTemplate.postForEntity(baseUrl, student, Student.class);
    }

    private ResponseEntity<Faculty> createTestFaculty(String name, String color) {
        Faculty faculty = new Faculty(null, name, color);
        return restTemplate.postForEntity(facultyUrl, faculty, Faculty.class);
    }

    private void clearAllStudents() {
        try {
            ResponseEntity<Student[]> response = restTemplate.getForEntity(baseUrl, Student[].class);
            if (response.getBody() != null) {
                for (Student student : response.getBody()) {
                    restTemplate.delete(baseUrl + "/" + student.getId());
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки при очистке
        }
    }

    private void clearAllFaculties() {
        try {
            ResponseEntity<Faculty[]> response = restTemplate.getForEntity(facultyUrl, Faculty[].class);
            if (response.getBody() != null) {
                for (Faculty faculty : response.getBody()) {
                    restTemplate.delete(facultyUrl + "/" + faculty.getId());
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки при очистке
        }
    }

    // ==================== CRUD ТЕСТЫ ====================

    @Test
    void testCreateStudent() {
        ResponseEntity<Student> response = createTestStudent("Тестовый Студент", 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Студент");
        assertThat(response.getBody().getAge()).isEqualTo(20);
    }

    @Test
    void testGetStudentById() {
        ResponseEntity<Student> createResponse = createTestStudent("Тестовый Студент", 20);
        Long id = Objects.requireNonNull(createResponse.getBody()).getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/" + id, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Студент");
        assertThat(response.getBody().getAge()).isEqualTo(20);
    }

    @Test
    void testGetStudentByIdNotFound() {
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/999999", Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testEditStudent() {
        ResponseEntity<Student> createResponse = createTestStudent("Тестовый Студент", 20);
        Long id = Objects.requireNonNull(createResponse.getBody()).getId();

        Student updatedStudent = new Student(id, "Обновленный Студент", 21);
        HttpEntity<Student> requestEntity = new HttpEntity<>(updatedStudent);
        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl, HttpMethod.PUT, requestEntity, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isEqualTo("Обновленный Студент");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void testEditStudentNotFound() {
        Student updatedStudent = new Student(999999L, "Несуществующий", 21);
        HttpEntity<Student> requestEntity = new HttpEntity<>(updatedStudent);
        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl, HttpMethod.PUT, requestEntity, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDeleteStudent() {
        ResponseEntity<Student> createResponse = createTestStudent("Тестовый Студент", 20);
        Long id = Objects.requireNonNull(createResponse.getBody()).getId();

        restTemplate.delete(baseUrl + "/" + id);

        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/" + id, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetAllStudents() {
        // Не очищаем базу, просто проверяем что запрос работает
        ResponseEntity<Student[]> response = restTemplate.getForEntity(baseUrl, Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    // ==================== ТЕСТЫ ФИЛЬТРАЦИИ ПО ВОЗРАСТУ ====================

    @Test
    void testGetStudentsByAge() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(baseUrl + "/age/17", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);

        Arrays.stream(response.getBody()).forEach(s -> assertThat(s.getAge()).isEqualTo(17));
    }

    @Test
    void testGetStudentsByAgeWithMultipleResults() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(baseUrl + "/age/17", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetStudentsByAgeWithNoResults() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/age/999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== ТЕСТЫ ФИЛЬТРАЦИИ ПО ДИАПАЗОНУ ====================

    @Test
    void testGetStudentsByAgeBetween() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                baseUrl + "/age-between?min=16&max=19", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);

        Arrays.stream(response.getBody()).forEach(s -> {
            assertThat(s.getAge()).isBetween(16, 19);
        });
    }

    @Test
    void testGetStudentsByAgeBetweenWithExactBounds() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                baseUrl + "/age-between?min=16&max=18", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);

        Arrays.stream(response.getBody()).forEach(s -> {
            assertThat(s.getAge()).isBetween(16, 18);
        });
    }

    @Test
    void testGetStudentsByAgeBetweenWithSingleResult() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                baseUrl + "/age-between?min=17&max=17", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);

        Arrays.stream(response.getBody()).forEach(s -> assertThat(s.getAge()).isEqualTo(17));
    }

    @Test
    void testGetStudentsByAgeBetweenWithAllAges() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                baseUrl + "/age-between?min=10&max=20", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void testGetStudentsByAgeBetweenWithNoResults() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/age-between?min=100&max=200", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetStudentsByAgeBetweenWithMinGreaterThanMax() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/age-between?min=20&max=15", String.class);

        // В зависимости от реализации может быть 400 BAD_REQUEST или 404 NOT_FOUND
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND);
    }

    // ==================== ТЕСТЫ СВЯЗЕЙ ====================

    @Test
    void testGetStudentFaculty() {
        // Создаем факультет
        ResponseEntity<Faculty> facultyResponse = createTestFaculty("Тестовый Факультет", "Красный");
        Long facultyId = Objects.requireNonNull(facultyResponse.getBody()).getId();

        // Создаем студента
        ResponseEntity<Student> studentResponse = createTestStudent("Тестовый Студент", 20);
        Long studentId = Objects.requireNonNull(studentResponse.getBody()).getId();

        // Назначаем студента на факультет
        restTemplate.put(baseUrl + "/" + studentId + "/faculty/" + facultyId, null);

        // Получаем факультет студента
        ResponseEntity<Faculty> response = restTemplate.getForEntity(baseUrl + "/" + studentId + "/faculty", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(facultyId);
    }

    @Test
    void testGetStudentFacultyNotFound() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(baseUrl + "/999999/faculty", Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testAssignStudentToFaculty() {
        // Создаем факультет
        ResponseEntity<Faculty> facultyResponse = createTestFaculty("Гриффиндор", "Красный");
        Long facultyId = Objects.requireNonNull(facultyResponse.getBody()).getId();

        // Создаем студента
        ResponseEntity<Student> studentResponse = createTestStudent("Гарри Поттер", 17);
        Long studentId = Objects.requireNonNull(studentResponse.getBody()).getId();

        // Назначаем студента на факультет
        restTemplate.put(baseUrl + "/" + studentId + "/faculty/" + facultyId, null);

        // Проверяем, что у студента есть факультет
        ResponseEntity<Student> checkResponse = restTemplate.getForEntity(baseUrl + "/" + studentId, Student.class);

        assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(checkResponse.getBody()).isNotNull();
        assertThat(checkResponse.getBody().getFaculty()).isNotNull();
        assertThat(checkResponse.getBody().getFaculty().getId()).isEqualTo(facultyId);
    }

    @Test
    void testGetStudentsByFaculty() {
        // Создаем факультет
        ResponseEntity<Faculty> facultyResponse = createTestFaculty("Гриффиндор", "Красный");
        Long facultyId = Objects.requireNonNull(facultyResponse.getBody()).getId();

        // Создаем студентов
        ResponseEntity<Student> studentResponse1 = createTestStudent("Гарри Поттер", 17);
        ResponseEntity<Student> studentResponse2 = createTestStudent("Гермиона Грейнджер", 17);

        Long studentId1 = Objects.requireNonNull(studentResponse1.getBody()).getId();
        Long studentId2 = Objects.requireNonNull(studentResponse2.getBody()).getId();

        // Назначаем студентов на факультет
        restTemplate.put(baseUrl + "/" + studentId1 + "/faculty/" + facultyId, null);
        restTemplate.put(baseUrl + "/" + studentId2 + "/faculty/" + facultyId, null);

        // Получаем студентов факультета
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                facultyUrl + "/" + facultyId + "/students", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
    }
}