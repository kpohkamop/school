package ru.hogwarts.school.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Map;

@Component
public class InitData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitData.class);

    private final StudentService studentService;
    private final FacultyService facultyService;

    public InitData(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== Начало загрузки тестовых данных ===");

        // Загружаем факультеты
        initFaculties();

        // Загружаем студентов
        initStudents();

        // Назначаем студентов на факультеты
        assignStudentsToFaculties();

        logger.info("=== Тестовые данные успешно загружены ===");
        logStatistics();
    }

    private void initFaculties() {
        if (facultyService.getAllFaculties().isEmpty()) {
            logger.info("Создание факультетов...");

            List<Faculty> faculties = List.of(
                    new Faculty(null, "Гриффиндор", "Красный"),
                    new Faculty(null, "Слизерин", "Зеленый"),
                    new Faculty(null, "Когтевран", "Синий"),
                    new Faculty(null, "Пуффендуй", "Желтый")
            );

            faculties.forEach(facultyService::createFaculty);
            logger.info("Создано {} факультетов", faculties.size());
        } else {
            logger.info("Факультеты уже существуют в базе данных");
        }
    }

    private void initStudents() {
        if (studentService.getAllStudents().isEmpty()) {
            logger.info("Создание студентов...");

            List<Student> students = List.of(
                    new Student(null, "Гарри Поттер", 17),
                    new Student(null, "Гермиона Грейнджер", 17),
                    new Student(null, "Рон Уизли", 17),
                    new Student(null, "Джинни Уизли", 16),
                    new Student(null, "Невилл Лонгботтом", 17),
                    new Student(null, "Фред Уизли", 19),
                    new Student(null, "Джордж Уизли", 19),
                    new Student(null, "Драко Малфой", 17),
                    new Student(null, "Винсент Крэбб", 17),
                    new Student(null, "Грегори Гойл", 17),
                    new Student(null, "Пэнси Паркинсон", 17),
                    new Student(null, "Блейз Забини", 17),
                    new Student(null, "Полумна Лавгуд", 16),
                    new Student(null, "Чжоу Чанг", 17),
                    new Student(null, "Падма Патил", 17),
                    new Student(null, "Майкл Корнер", 17),
                    new Student(null, "Седрик Диггори", 18),
                    new Student(null, "Джастин Финч-Флетчли", 17),
                    new Student(null, "Ханна Аббот", 17),
                    new Student(null, "Эрни Макмиллан", 17)
            );

            students.forEach(studentService::createStudent);
            logger.info("Создано {} студентов", students.size());
        } else {
            logger.info("Студенты уже существуют в базе данных");
        }
    }

    private void assignStudentsToFaculties() {
        logger.info("Назначение студентов на факультеты...");

        // Получаем все факультеты и студентов
        List<Faculty> faculties = List.copyOf(facultyService.getAllFaculties());
        List<Student> students = List.copyOf(studentService.getAllStudents());

        if (students.isEmpty() || faculties.isEmpty()) {
            logger.warn("Невозможно назначить студентов: нет факультетов или студентов");
            return;
        }

        // Распределение студентов по факультетам
        Map<String, List<String>> facultyAssignments = Map.of(
                "Гриффиндор", List.of("Гарри Поттер", "Гермиона Грейнджер", "Рон Уизли",
                        "Джинни Уизли", "Невилл Лонгботтом", "Фред Уизли", "Джордж Уизли"),
                "Слизерин", List.of("Драко Малфой", "Винсент Крэбб", "Грегори Гойл",
                        "Пэнси Паркинсон", "Блейз Забини"),
                "Когтевран", List.of("Полумна Лавгуд", "Чжоу Чанг", "Падма Патил", "Майкл Корнер"),
                "Пуффендуй", List.of("Седрик Диггори", "Джастин Финч-Флетчли", "Ханна Аббот", "Эрни Макмиллан")
        );

        int assignedCount = 0;
        for (Student student : students) {
            for (Faculty faculty : faculties) {
                if (facultyAssignments.getOrDefault(faculty.getName(), List.of())
                        .contains(student.getName())) {
                    student.setFaculty(faculty);
                    studentService.editStudent(student);
                    assignedCount++;
                    logger.debug("Студент {} назначен на факультет {}",
                            student.getName(), faculty.getName());
                    break;
                }
            }
        }

        logger.info("Назначение студентов на факультеты завершено. Назначено {} студентов", assignedCount);
    }

    private void logStatistics() {
        long facultyCount = facultyService.getAllFaculties().size();
        long studentCount = studentService.getAllStudents().size();

        logger.info("Статистика базы данных:");
        logger.info("- Факультетов: {}", facultyCount);
        logger.info("- Студентов: {}", studentCount);

        // УБРАЛИ ПРОБЛЕМНЫЙ КОД!
        // Вместо попытки получить students из faculty (что вызывает LazyInitializationException),
        // просто выводим информационное сообщение
        logger.info("Для просмотра студентов факультета используйте API: GET /faculty/{id}/students");
        logger.info("Для просмотра факультета студента используйте API: GET /student/{id}/faculty");
    }
}