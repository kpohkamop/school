-- Создание индекса для поиска по имени студента
-- Индекс ускоряет запросы вида: SELECT * FROM student WHERE name = ?;
CREATE INDEX IF NOT EXISTS idx_student_name ON student (name);