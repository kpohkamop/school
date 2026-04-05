package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/stream")
public class StreamController {

    private static final Logger logger = LoggerFactory.getLogger(StreamController.class);

    private final StudentService studentService;
    private final FacultyService facultyService;

    @Autowired
    public StreamController(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    /**
     * Шаг 1: Получить имена студентов, начинающиеся на "А"
     * GET /stream/students/names-starting-with-a
     */
    @GetMapping("/students/names-starting-with-a")
    public List<String> getStudentNamesStartingWithA() {
        logger.info("Was invoked method for get student names starting with A");
        return studentService.getStudentNamesStartingWithA();
    }

    /**
     * Шаг 2: Получить средний возраст всех студентов
     * GET /stream/students/average-age
     */
    @GetMapping("/students/average-age")
    public double getAverageAgeOfAllStudents() {
        logger.info("Was invoked method for get average age of all students");
        return studentService.getAverageAgeOfAllStudents();
    }

    /**
     * Шаг 3: Получить самое длинное название факультета
     * GET /stream/faculties/longest-name
     */
    @GetMapping("/faculties/longest-name")
    public String getLongestFacultyName() {
        logger.info("Was invoked method for get longest faculty name");
        return facultyService.getLongestFacultyName();
    }

    /**
     * Шаг 4: Параллельные стримы - оптимизированная сумма
     * GET /stream/sum
     *
     * Обычная версия (медленная):
     * int sum = Stream.iterate(1, a -> a + 1).limit(1_000_000).reduce(0, (a, b) -> a + b);
     *
     * Оптимизированная версия (быстрая):
     * 1. Используем parallel() для параллельного вычисления
     * 2. Используем mapToInt и sum() вместо reduce
     */
    @GetMapping("/sum")
    public int getParallelSum() {
        logger.info("Was invoked method for get parallel sum of 1 to 1,000,000");

        long startTime = System.currentTimeMillis();

        // ОПТИМИЗИРОВАННАЯ ВЕРСИЯ
        int sum = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()                    // ← КЛЮЧЕВОЙ МОМЕНТ! Параллельное выполнение
                .mapToInt(Integer::intValue)
                .sum();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Sum = {}, computed in {} ms", sum, duration);
        logger.debug("Parallel stream used for optimization");

        return sum;
    }

    /**
     * Альтернативная оптимизированная версия с использованием формулы
     * GET /stream/sum-fast
     *
     * Формула суммы арифметической прогрессии: n * (n + 1) / 2
     * Это O(1) вместо O(n)
     */
    @GetMapping("/sum-fast")
    public int getFastSum() {
        logger.info("Was invoked method for get fast sum using formula");

        long startTime = System.currentTimeMillis();

        // Математическая формула - самое быстрое решение
        int n = 1_000_000;
        int sum = n * (n + 1) / 2;

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Sum = {}, computed in {} ms (using formula)", sum, duration);

        return sum;
    }

    /**
     * Сравнение производительности (для демонстрации)
     * GET /stream/sum/compare
     */
    @GetMapping("/sum/compare")
    public String comparePerformance() {
        logger.info("Was invoked method for compare performance");

        StringBuilder result = new StringBuilder();

        // 1. Обычный последовательный стрим
        long start1 = System.currentTimeMillis();
        int sum1 = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .reduce(0, (a, b) -> a + b);
        long time1 = System.currentTimeMillis() - start1;

        // 2. Параллельный стрим
        long start2 = System.currentTimeMillis();
        int sum2 = Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .mapToInt(Integer::intValue)
                .sum();
        long time2 = System.currentTimeMillis() - start2;

        // 3. Математическая формула
        long start3 = System.currentTimeMillis();
        int n = 1_000_000;
        int sum3 = n * (n + 1) / 2;
        long time3 = System.currentTimeMillis() - start3;

        result.append("Сравнение производительности вычисления суммы 1..1,000,000:\n\n");
        result.append("1. Обычный sequential stream: ").append(time1).append(" ms, результат: ").append(sum1).append("\n");
        result.append("2. Параллельный parallel stream: ").append(time2).append(" ms, результат: ").append(sum2).append("\n");
        result.append("3. Математическая формула: ").append(time3).append(" ms, результат: ").append(sum3).append("\n\n");
        result.append("Вывод: parallel stream быстрее sequential, но формула - самое быстрое решение!");

        return result.toString();
    }
}