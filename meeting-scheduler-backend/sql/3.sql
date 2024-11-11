delete from availabilities;
delete from users;

INSERT INTO users (id, name, time_zone) VALUES
                                            (1, 'User1', 'Europe/London'),
                                            (2, 'User2', 'Europe/Kyiv');

-- User1's availability on Tuesday
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
    (1, 1, 'TUESDAY', '18:00:00', '23:00:00');

-- User2's availability on Tuesday with a slight overlap with User1
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
    (2, 2, 'WEDNESDAY', '00:00:00', '03:00:00');

INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
    (3, 2, 'TUESDAY', '21:00:00', '23:00:00');