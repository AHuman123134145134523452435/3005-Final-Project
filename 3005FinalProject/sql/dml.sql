INSERT INTO Members (first_name, last_name, email, phone, birth_date, gender)
VALUES 
('Alice', 'Smith', 'alice.smith@email.com', '123-456-7890', '2003-08-25', 'Female'),
('Bob', 'Johnson', 'bob.johnson@email.com', '234-567-8901', '2003-09-01', 'Male'),
('Charlie', 'Williams', 'charlie.williams@email.com', '345-678-9012', '2003-08-20', 'Male'),
('David', 'Brown', 'david.brown@email.com', '456-789-0123', '2003-09-14', 'Male');

insert into Trainers (start_times, end_times, first_name, last_name, email)
values ('{"2023-09-14 3:10:00"}', '{"2023-09-14 4:50:00"}', 'Julius', 'Caesar', 'julius.caesar@email.com'),
('{}', '{}', 'Marcus', 'Cicero', 'marcus.cicero@email.com'),
('{}', '{}', 'Aulus', 'Hirtius', 'aulus.hirtius@email.com');

insert into fitgoals (goal_name, goal_number, member_email)
values 
('Weight loss', 140, 'alice.smith@email.com'),
('Get taller', 186, 'bob.johnson@email.com'),
('Heart Rate Down', 75, 'alice.smith@email.com');

insert into healthmetr(height, weight_, heart_rate, metric_timestamp, member_email) 
values 
(167, 120, 73, '2023-08-20 12:30:00', 'david.brown@email.com'),
(168, 110, 73, '2023-09-13 12:30:00', 'david.brown@email.com'),
(180, 143, 81, '2023-08-25 01:43:54', 'bob.johnson@email.com');

insert into classes (start_time, end_time, room_number, trainer_id, capacity, member_email)
values ('2023-09-14 3:15:00', '2023-09-14 04:45:00', 2, 1, 4, '{"charlie.william@email.com"}'),
('2023-09-14 1:30:00', '2023-09-14 03:00:00', 3, 1, 3, '{"david.brown@email.com"}'),
('2023-09-14 7:00:00', '2023-09-14 08:45:00', 1, 1, 5, '{"charlie.william@email.com"}');

insert into sessions (start_time, end_time, room_number, trainer_id, member_email)
values ('2023-09-15 3:15:00', '2023-09-15 04:45:00', 2, 2, 'bob.johnson@email.com'),
('2023-09-15 1:30:00', '2023-09-15 01:00:00', 2, 3, 'alice.smith@email.com'),
('2023-09-15 7:15:00', '2023-09-15 08:45:00', 3, 3, 'alice.smith@email.com');

insert into rooms(class_id, session_id) values('{3}', '{}'), ('{1}', '{1, 2}'), ('{2}', '{3}');