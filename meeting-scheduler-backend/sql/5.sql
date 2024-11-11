-- Test: testUsersInDifferentTimeZones_ReturnsSlotsInUTC
DELETE FROM availabilities;
DELETE FROM users;

-- Insert users with specified time zones
INSERT INTO users (id, name, time_zone) VALUES
                                            (1, 'User1', 'America/New_York'),
                                            (2, 'User2', 'UTC');

-- Insert availabilities for Monday, with user1's times converted to UTC
INSERT INTO availabilities (id, user_id, day_of_week, start_time, end_time) VALUES
                                            (1, 1, 'MONDAY', '09:00:00', '15:00:00'),
                                            (2, 2, 'MONDAY', '10:00:00', '18:00:00');
-- duration = 2
-- meeting slots: 14:00, 15:00, 16:00