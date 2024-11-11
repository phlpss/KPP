delete from availabilities;
delete from users;

INSERT INTO users (id, name, time_zone) VALUES
                                            (1, 'User1', 'Europe/London'),
                                            (2, 'User2', 'Europe/Prague');

-- User1's availability on Tuesday
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
    (1, 1, 'TUESDAY', '11:00:00', '15:00:00');

-- User2's availability on Tuesday with a slight overlap with User1
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
    (2, 2, 'TUESDAY', '12:00:00', '14:00:00');

-- user1 11 - 15 in utc
-- user2 11 - 13 in utc