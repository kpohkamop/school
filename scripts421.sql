-- 1. Ограничение возраста студента: возраст не может быть меньше 16 лет
ALTER TABLE student
ADD CONSTRAINT student_age_check CHECK (age >= 16);

-- 2. Имена студентов должны быть уникальными и не равны NULL
ALTER TABLE student
ADD CONSTRAINT student_name_unique UNIQUE (name);

ALTER TABLE student
ALTER COLUMN name SET NOT NULL;

-- 3. Пара "название" - "цвет факультета" должна быть уникальной
ALTER TABLE faculty
ADD CONSTRAINT faculty_name_color_unique UNIQUE (name, color);

-- 4. При создании студента без возраста ему автоматически присваивается 20 лет
ALTER TABLE student
ALTER COLUMN age SET DEFAULT 20;

-- =====================================================
-- ПРОВЕРОЧНЫЕ ЗАПРОСЫ (для тестирования)
-- =====================================================

-- Проверка ограничений:
-- INSERT INTO student (name, age) VALUES ('Тест', 15); -- Будет ошибка: age >= 16
-- INSERT INTO student (name, age) VALUES ('Тест', NULL); -- NULL заменится на 20
-- INSERT INTO student (name, age) VALUES ('Дубликат', 20);
-- INSERT INTO student (name, age) VALUES ('Дубликат', 21); -- Будет ошибка: дубликат имени
-- INSERT INTO faculty (name, color) VALUES ('Гриффиндор', 'Красный');
-- INSERT INTO faculty (name, color) VALUES ('Гриффиндор', 'Золотой'); -- Ошибка: дубликат пары