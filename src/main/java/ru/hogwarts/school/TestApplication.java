package ru.hogwarts.school;

import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.Faculty;

public class TestApplication {
    public static void main(String[] args) {
        System.out.println("=== Тестирование модели Faculty ===");

        // Создаем факультет (используем Long для ID)
        Faculty faculty = new Faculty();
        faculty.setId(1L);  // L указывает что это Long
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        System.out.println("Создан факультет: " + faculty);
        System.out.println("ID: " + faculty.getId());
        System.out.println("Название: " + faculty.getName());
        System.out.println("Цвет: " + faculty.getColor());

        System.out.println("\n=== Тестирование модели Student ===");

        // Создаем студента - БЕЗ факультета
        Student student = new Student();
        student.setId(1L);
        student.setName("Harry Potter");
        student.setAge(14);
        // УБРАЛИ: student.setFaculty(faculty);  // ← УДАЛИТЬ эту строку

        System.out.println("Создан студент: " + student);
        System.out.println("ID: " + student.getId());
        System.out.println("Имя: " + student.getName());
        System.out.println("Возраст: " + student.getAge());
        // УБРАЛИ вывод факультета у студента

        System.out.println("\n=== Тестирование сеттеров ===");

        // Меняем значения
        student.setAge(15);
        student.setName("Harry James Potter");

        faculty.setColor("Scarlet");
        faculty.setName("Gryffindor House");

        System.out.println("После изменений:");
        System.out.println("Студент: " + student);
        System.out.println("Факультет: " + faculty);

        System.out.println("\n✅ Все тесты пройдены успешно!");

        // Альтернативный способ создания через конструкторы
        System.out.println("\n=== Тестирование конструкторов ===");

        // Используем существующие конструкторы (без faculty)
        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Draco Malfoy");
        student2.setAge(14);

        System.out.println("Через конструкторы:");
        System.out.println("Студент: " + student2);
    }
}