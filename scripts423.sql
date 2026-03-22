-- 1. Получить информацию обо всех студентах (имя и возраст)
--    вместе с названиями факультетов
--    Используем LEFT JOIN, чтобы показать студентов без факультета
SELECT
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name
FROM student s
LEFT JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.id;

-- Альтернативный вариант с INNER JOIN (только студенты с факультетом)
-- SELECT
--     s.name AS student_name,
--     s.age AS student_age,
--     f.name AS faculty_name
-- FROM student s
-- INNER JOIN faculty f ON s.faculty_id = f.id
-- ORDER BY s.id;

-- =====================================================
-- 2. Получить только тех студентов, у которых есть аватарки
-- =====================================================

-- Вариант 1: с INNER JOIN (только студенты, у которых есть аватарка)
SELECT
    s.id,
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name,
    a.file_path,
    a.file_size,
    a.media_type
FROM student s
INNER JOIN avatar a ON s.id = a.student_id
LEFT JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.id;

-- Вариант 2: упрощенный вариант (только имена студентов)
SELECT
    s.id,
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name
FROM student s
INNER JOIN avatar a ON s.id = a.student_id
LEFT JOIN faculty f ON s.faculty_id = f.id
ORDER BY s.id;

-- Вариант 3: только имена студентов с аватарками (минимальный набор)
SELECT
    s.name AS student_name
FROM student s
INNER JOIN avatar a ON s.id = a.student_id;

-- =====================================================
-- ДОПОЛНИТЕЛЬНЫЕ ЗАПРОСЫ ДЛЯ ПРОВЕРКИ
-- =====================================================

-- Проверить количество студентов с аватарками
SELECT COUNT(DISTINCT s.id) AS students_with_avatars
FROM student s
INNER JOIN avatar a ON s.id = a.student_id;

-- Проверить количество студентов без аватарок
SELECT COUNT(*) AS students_without_avatars
FROM student s
LEFT JOIN avatar a ON s.id = a.student_id
WHERE a.id IS NULL;