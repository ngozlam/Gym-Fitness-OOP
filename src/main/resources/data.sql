-- Xóa data cũ nếu có (chạy lại nhiều lần không bị lỗi)
DELETE FROM attendance;
DELETE FROM payments;
DELETE FROM subscriptions;
DELETE FROM members;
DELETE FROM membership_plans;
DELETE FROM trainers;
DELETE FROM staff;
INSERT INTO staff (username, password_hash, full_name, role) VALUES
('admin', '$2a$10$tQjhiYfz/fcwXy9.rJswweFxN7ujGsE4Q0b29Fo8S6xQuNJe9HVP6', 'Nguyen Van Admin', 'ADMIN'),
('trainer1', '$2a$10$tQjhiYfz/fcwXy9.rJswweFxN7ujGsE4Q0b29Fo8S6xQuNJe9HVP6', 'Tran Van Manh', 'TRAINER'),
('receptionist1', '$2a$10$tQjhiYfz/fcwXy9.rJswweFxN7ujGsE4Q0b29Fo8S6xQuNJe9HVP6', 'Le Thi Hoa', 'RECEPTIONIST');

-- Trainers
INSERT INTO trainers (full_name, specialty, phone) VALUES
('Tran Van Manh', 'Yoga', '0901234567'),
('Nguyen Thi Lan', 'Boxing', '0902345678'),
('Le Van Hung', 'Cardio', '0903456789');

-- Members
INSERT INTO members (full_name, email, phone, date_of_birth, joined_date, trainer_id) VALUES
('Pham Van An', 'an@gmail.com', '0911111111', '2000-01-15', '2026-01-01', 1),
('Nguyen Thi Bich', 'bich@gmail.com', '0922222222', '1995-05-20', '2026-02-01', 2),
('Tran Van Cuong', 'cuong@gmail.com', '0933333333', '1998-08-10', '2026-03-01', 3);

-- Membership Plans
INSERT INTO membership_plans (name, duration_months, base_price, pricing_strategy) VALUES
('Monthly', 1, 300000, 'STANDARD'),
('Quarterly', 3, 800000, 'STANDARD'),
('Annual', 12, 2500000, 'STUDENT_DISCOUNT');

-- Subscriptions
INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status, final_price) VALUES
(1, 1, '2026-01-01', '2026-02-01', 'EXPIRED', 300000),
(2, 2, '2026-02-01', '2026-05-01', 'ACTIVE', 800000),
(3, 3, '2026-03-01', '2027-03-01', 'ACTIVE', 2500000);

-- Payments
INSERT INTO payments (subscription_id, amount, payment_date, method) VALUES
(1, 300000, '2026-01-01 09:00:00', 'CASH'),
(2, 800000, '2026-02-01 10:00:00', 'TRANSFER'),
(3, 2500000, '2026-03-01 11:00:00', 'CARD');

-- Attendance
INSERT INTO attendance (member_id, check_in, check_out) VALUES
(1, '2026-01-05 08:00:00', '2026-01-05 10:00:00'),
(2, '2026-02-10 07:30:00', '2026-02-10 09:30:00'),
(3, '2026-03-15 06:00:00', '2026-03-15 08:00:00');