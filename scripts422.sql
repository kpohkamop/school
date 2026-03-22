-- 1. Создание таблицы "Машина" (Car)
CREATE TABLE IF NOT EXISTS car (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price DECIMAL(15, 2) NOT NULL CHECK (price >= 0)
);

-- 2. Создание таблицы "Человек" (Person)
-- У каждого человека есть машина (несколько человек могут пользоваться одной машиной)
CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL CHECK (age >= 0),
    has_license BOOLEAN NOT NULL DEFAULT FALSE,
    car_id BIGINT,
    CONSTRAINT fk_person_car FOREIGN KEY (car_id) REFERENCES car(id) ON DELETE SET NULL
);

-- =====================================================
-- ДОПОЛНИТЕЛЬНЫЕ ИНДЕКСЫ ДЛЯ ОПТИМИЗАЦИИ
-- =====================================================

-- Индекс для внешнего ключа
CREATE INDEX idx_person_car_id ON person(car_id);

-- =====================================================
-- ТЕСТОВЫЕ ДАННЫЕ (для проверки)
-- =====================================================

-- Вставка машин
INSERT INTO car (brand, model, price) VALUES
('Toyota', 'Camry', 2500000.00),
('BMW', 'X5', 5500000.00),
('Mercedes-Benz', 'E-Class', 4800000.00),
('Kia', 'Rio', 1200000.00),
('Hyundai', 'Solaris', 1100000.00);

-- Вставка людей (несколько человек могут пользоваться одной машиной)
INSERT INTO person (name, age, has_license, car_id) VALUES
('Иван Петров', 25, true, 1),
('Мария Иванова', 30, true, 1),      -- та же машина (Toyota Camry)
('Алексей Сидоров', 19, true, 2),
('Ольга Смирнова', 22, true, 3),
('Дмитрий Козлов', 35, true, 3),      -- та же машина (Mercedes-Benz)
('Елена Новикова', 18, false, NULL),  -- без машины
('Андрей Морозов', 40, true, 4),
('Татьяна Волкова', 28, true, 5);

-- =====================================================
-- ПРОВЕРОЧНЫЕ ЗАПРОСЫ
-- =====================================================

-- Получить всех людей с их машинами
SELECT p.name, p.age, p.has_license, c.brand, c.model, c.price
FROM person p
LEFT JOIN car c ON p.car_id = c.id;

-- Получить количество людей на каждой машине
SELECT c.brand, c.model, COUNT(p.id) as person_count
FROM car c
LEFT JOIN person p ON p.car_id = c.id
GROUP BY c.id, c.brand, c.model
ORDER BY person_count DESC;