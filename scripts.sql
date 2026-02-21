-- =====================================================
-- SQL ЗАПРОСЫ ДЛЯ БАЗЫ ДАННЫХ ХОГВАРТС
-- =====================================================

-- 1. Получить всех студентов, возраст которых находится между 10 и 20
SELECT * FROM student
WHERE age BETWEEN 10 AND 20;

-- 2. Получить всех студентов, но отобразить только список их имен
SELECT name FROM student;

-- 3. Получить всех студентов, у которых в имени присутствует буква 'о'
SELECT * FROM student
WHERE name LIKE '%о%'
   OR name LIKE '%О%';

-- 4. Получить всех студентов, у которых возраст меньше идентификатора
SELECT * FROM student
WHERE age < id;

-- 5. Получить всех студентов упорядоченных по возрасту
SELECT * FROM student
ORDER BY age;

-- =====================================================
-- ДОПОЛНИТЕЛЬНЫЕ ЗАПРОСЫ ДЛЯ ПРОВЕРКИ
-- =====================================================

-- Получить все факультеты
SELECT * FROM faculty;

-- Получить количество студентов на каждом факультете
SELECT f.name, COUNT(s.id) as student_count
FROM faculty f
LEFT JOIN student s ON s.faculty_id = f.id
GROUP BY f.id, f.name;

-- Получить студентов Гриффиндора (после добавления связи ManyToOne)
SELECT s.* FROM student s
JOIN faculty f ON s.faculty_id = f.id
WHERE f.name = 'Gryffindor';
