delete from availabilities;
delete from users;

INSERT INTO users (id, name, time_zone) VALUES
                                            (1, 'User1', 'Europe/London'),
                                            (2, 'User2', 'Europe/London');

-- User1's availability on Tuesday
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
    (1, 1, 'TUESDAY', '11:00:00', '15:00:00');

-- User2's availability on Tuesday with a slight overlap with User1
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
    (2, 2, 'TUESDAY', '12:00:00', '14:00:00');

-- Additional test case for non-consecutive hours
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
                                                                                (3, 1, 'WEDNESDAY', '11:00:00', '13:00:00'),
                                                                                (4, 2, 'WEDNESDAY', '14:00:00', '16:00:00');

-- max 2 hour meeting on tuesday, none on wednesday