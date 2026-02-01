package ru.hogwarts.school.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.FacultyService;

@Component
public class InitData implements CommandLineRunner {

    private final StudentService studentService;
    private final FacultyService facultyService;

    public InitData(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Загрузка начальных данных в базу данных ===");

        // Проверяем, есть ли уже данные в базе
        if (facultyService.getAllFaculties().isEmpty()) {
            loadFaculties();
        }

        if (studentService.getAllStudents().isEmpty()) {
            loadStudents();
        }

        System.out.println("=== Начальные данные успешно загружены ===");
    }

    private void loadFaculties() {
        System.out.println("Создание факультетов...");

        Faculty gryffindor = new Faculty(null, "Gryffindor", "Red");
        Faculty slytherin = new Faculty(null, "Slytherin", "Green");
        Faculty ravenclaw = new Faculty(null, "Ravenclaw", "Blue");
        Faculty hufflepuff = new Faculty(null, "Hufflepuff", "Yellow");

        facultyService.createFaculty(gryffindor);
        facultyService.createFaculty(slytherin);
        facultyService.createFaculty(ravenclaw);
        facultyService.createFaculty(hufflepuff);

        System.out.println("Факультеты созданы: Gryffindor, Slytherin, Ravenclaw, Hufflepuff");
    }

    private void loadStudents() {
        System.out.println("Создание студентов...");

        // Студенты Гриффиндора
        studentService.createStudent(new Student(null, "Гарри Поттер", 17));
        studentService.createStudent(new Student(null, "Гермиона Грейнджер", 17));
        studentService.createStudent(new Student(null, "Рон Уизли", 17));
        studentService.createStudent(new Student(null, "Джинни Уизли", 16));
        studentService.createStudent(new Student(null, "Невилл Лонгботтом", 17));
        studentService.createStudent(new Student(null, "Фред Уизли", 19));
        studentService.createStudent(new Student(null, "Джордж Уизли", 19));

        // Студенты Слизерина
        studentService.createStudent(new Student(null, "Драко Малфой", 17));
        studentService.createStudent(new Student(null, "Винсент Крэбб", 17));
        studentService.createStudent(new Student(null, "Грегори Гойл", 17));
        studentService.createStudent(new Student(null, "Пэнси Паркинсон", 17));
        studentService.createStudent(new Student(null, "Блейз Забини", 17));

        // Студенты Когтеврана
        studentService.createStudent(new Student(null, "Полумна Лавгуд", 16));
        studentService.createStudent(new Student(null, "Чжоу Чанг", 17));
        studentService.createStudent(new Student(null, "Падма Патил", 17));
        studentService.createStudent(new Student(null, "Майкл Корнер", 17));

        // Студенты Пуффендуя
        studentService.createStudent(new Student(null, "Седрик Диггори", 18));
        studentService.createStudent(new Student(null, "Джастин Финч-Флетчли", 17));
        studentService.createStudent(new Student(null, "Ханна Аббот", 17));
        studentService.createStudent(new Student(null, "Эрни Макмиллан", 17));

        System.out.println("Создано 20 студентов Хогвартса");
    }
}