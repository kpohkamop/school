-- Очистка таблиц (опционально, если ddl-auto=create-drop)
-- DELETE FROM student;
-- DELETE FROM faculty;

-- Вставка факультетов
INSERT INTO faculty (name, color) VALUES ('Гриффиндор', 'Красный');
INSERT INTO faculty (name, color) VALUES ('Слизерин', 'Зеленый');
INSERT INTO faculty (name, color) VALUES ('Когтевран', 'Синий');
INSERT INTO faculty (name, color) VALUES ('Пуффендуй', 'Желтый');

-- Вставка студентов
INSERT INTO student (name, age) VALUES ('Гарри Поттер', 17);
INSERT INTO student (name, age) VALUES ('Гермиона Грейнджер', 17);
INSERT INTO student (name, age) VALUES ('Рон Уизли', 17);
INSERT INTO student (name, age) VALUES ('Драко Малфой', 17);
INSERT INTO student (name, age) VALUES ('Полумна Лавгуд', 16);
INSERT INTO student (name, age) VALUES ('Невилл Лонгботтом', 17);
INSERT INTO student (name, age) VALUES ('Джинни Уизли', 16);
INSERT INTO student (name, age) VALUES ('Седрик Диггори', 18);