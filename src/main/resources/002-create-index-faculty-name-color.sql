-- Индекс ускоряет запросы вида: SELECT * FROM faculty WHERE name = ? AND color = ?;
CREATE INDEX IF NOT EXISTS idx_faculty_name_color ON faculty (name, color);
