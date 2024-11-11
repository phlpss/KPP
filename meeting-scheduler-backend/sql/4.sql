-- Clear existing data
DELETE FROM availabilities;
DELETE FROM users;

-- Insert users with UTC time zone
INSERT INTO users (id, name, time_zone) VALUES
                                            (1, 'User1', 'UTC'),
                                            (2, 'User2', 'UTC'),
                                            (3, 'User3', 'UTC'),
                                            (4, 'User4', 'UTC');

-- Insert availabilities on Monday with overlapping times
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
                                            (1, 1, 'MONDAY', '09:00:00', '15:00:00'),
                                            (2, 2, 'MONDAY', '10:00:00', '15:00:00'),
                                            (3, 3, 'MONDAY', '11:00:00', '14:00:00'),
                                            (4, 4, 'MONDAY', '12:00:00', '17:00:00');