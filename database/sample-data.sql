-- =============================================================================
-- CampusSphere: Academic Sample & Demo Seed Data
-- =============================================================================
-- USAGE INSTRUCTIONS:
-- 1. Run this file ONCE on an initialized database with empty tables as MySQL root.
-- 2. Command in PowerShell:
--    & "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "source database/sample-data.sql"
-- 3. DO NOT run this script on an active production database.
-- =============================================================================

USE campussphere;

-- -----------------------------------------------------------------------------
-- 1. Seed Clubs
-- -----------------------------------------------------------------------------
INSERT INTO clubs (name, description, contact_email, image_url) VALUES
('Coding Club', 
 'The premier student software development and competitive programming society. Organizing weekly coding contests, hackathons, and open source workshops.',
 'coding@campussphere.example', 
 NULL),

('Robotics & Automation Society', 
 'Designing autonomous robots, drone technology, and IoT embedded hardware systems. Fostering hardware engineering and inter-college challenge teams.',
 'robotics@campussphere.example', 
 NULL),

('Cultural Society', 
 'Celebrating performing arts, music, dance, and theater. Hosting campus-wide cultural festivals, acoustic evenings, and drama competitions.',
 'cultural@campussphere.example', 
 NULL),

('Literary & Debating Society', 
 'Fostering critical discourse, parliamentary debates, creative writing, and poetry recitals. Representing the college at national youth parliaments.',
 'literary@campussphere.example', 
 NULL);

-- -----------------------------------------------------------------------------
-- 2. Seed Demo Students
-- Note: password_hash uses '!NO_LOGIN_DEMO_ACCOUNT!' which is intentionally invalid
-- for PBKDF2 verification. Phase 5 password authentication will strictly reject this.
-- Real user accounts are registered through the web portal.
-- -----------------------------------------------------------------------------
INSERT INTO students (full_name, email, password_hash, department, year_of_study, phone) VALUES
('Aarav Sharma', 
 'aarav.sharma@example.test', 
 '!NO_LOGIN_DEMO_ACCOUNT!', 
 'Computer Science & Engineering', 
 3, 
 '+91 98765 43210'),

('Diya Patel', 
 'diya.patel@example.test', 
 '!NO_LOGIN_DEMO_ACCOUNT!', 
 'Electronics & Communication', 
 2, 
 '+91 98765 43211'),

('Rohan Verma', 
 'rohan.verma@example.test', 
 '!NO_LOGIN_DEMO_ACCOUNT!', 
 'Mechanical Engineering', 
 4, 
 '+91 98765 43212');

-- -----------------------------------------------------------------------------
-- 3. Seed Events (Relative Dates to CURDATE())
-- -----------------------------------------------------------------------------
-- Event 1: Upcoming Open Technical Hackathon (Capacity 100)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Campus Hackathon 2026', 
       'An intensive 24-hour collaborative hackathon where student teams design and build innovative web and AI solutions. Mentorship from industry engineers, free food, and exciting hardware prizes for top prototypes.',
       'Technical', 
       'Main Auditorium & Computing Center', 
       DATE_ADD(CURDATE(), INTERVAL 14 DAY), 
       '09:00:00', 
       '21:00:00', 
       DATE_ADD(NOW(), INTERVAL 10 DAY), 
       100, 
       NULL
FROM clubs WHERE name = 'Coding Club';

-- Event 2: Upcoming Open Workshop on IoT & Robotics (Capacity 40)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Hands-On Microcontroller & IoT Workshop', 
       'Learn how to interface ESP32 microcontrollers with cloud MQTT brokers, read environmental sensors, and build your first home automation gadget in this hands-on lab.',
       'Workshop', 
       'Robotics Lab, Room 204', 
       DATE_ADD(CURDATE(), INTERVAL 7 DAY), 
       '14:00:00', 
       '17:30:00', 
       DATE_ADD(NOW(), INTERVAL 5 DAY), 
       40, 
       NULL
FROM clubs WHERE name = 'Robotics & Automation Society';

-- Event 3: Upcoming Cultural Evening (Unlimited Capacity: NULL)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Annual Campus Acoustic & Open Mic Night', 
       'An open-air evening showcasing student musicians, vocalists, acoustic bands, and spoken-word poets under the campus amphitheater lights. Refreshments provided.',
       'Cultural', 
       'Open Air Amphitheater', 
       DATE_ADD(CURDATE(), INTERVAL 10 DAY), 
       '18:00:00', 
       '21:30:00', 
       DATE_ADD(NOW(), INTERVAL 9 DAY), 
       NULL, 
       NULL
FROM clubs WHERE name = 'Cultural Society';

-- Event 4: Upcoming Parliamentary Debate (Capacity 50)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Inter-Department Parliamentary Debate Tournament', 
       'Teams of three debaters clash on pressing global ethics, environmental legislation, and technological policy in Asian Parliamentary debate format.',
       'Seminar', 
       'Seminar Hall B, Academic Block 1', 
       DATE_ADD(CURDATE(), INTERVAL 20 DAY), 
       '10:00:00', 
       '16:00:00', 
       DATE_ADD(NOW(), INTERVAL 15 DAY), 
       50, 
       NULL
FROM clubs WHERE name = 'Literary & Debating Society';

-- Event 5: Upcoming Sports Chess Championship (Capacity 32)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Rapid Blitz Chess Championship', 
       'Five-round Swiss-system blitz chess competition for all skill levels. FIDE time control: 10 minutes plus 5 seconds increment per move.',
       'Sports', 
       'Student Activity Center', 
       DATE_ADD(CURDATE(), INTERVAL 25 DAY), 
       '11:00:00', 
       '17:00:00', 
       DATE_ADD(NOW(), INTERVAL 22 DAY), 
       32, 
       NULL
FROM clubs WHERE name = 'Coding Club';

-- Event 6: FULL Event (Capacity 2, Registered 2)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Elite Drone Piloting Masterclass', 
       'Specialized masterclass with limited flight simulator rigs covering FPV racing quadcopter piloting and aerial cinematography.',
       'Workshop', 
       'Aeronautics Hangar 3', 
       DATE_ADD(CURDATE(), INTERVAL 5 DAY), 
       '13:00:00', 
       '16:00:00', 
       DATE_ADD(NOW(), INTERVAL 3 DAY), 
       2, 
       NULL
FROM clubs WHERE name = 'Robotics & Automation Society';

-- Event 7: CLOSED Event (Registration deadline passed yesterday, event is in future)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Cybersecurity Defense & Red Teaming Boot Camp', 
       'Advanced capture-the-flag (CTF) security training. Registration has closed to prepare isolated virtual machine credentials for participants.',
       'Technical', 
       'Cyber Labs Room 101', 
       DATE_ADD(CURDATE(), INTERVAL 3 DAY), 
       '10:00:00', 
       '15:00:00', 
       DATE_SUB(NOW(), INTERVAL 1 DAY), 
       25, 
       NULL
FROM clubs WHERE name = 'Coding Club';

-- Event 8: ENDED Event (Past event from last month)
INSERT INTO events (club_id, title, description, category, venue, event_date, start_time, end_time, registration_deadline, capacity, image_url)
SELECT id, 
       'Orientation & Welcome Induction 2026', 
       'Welcome orientation showcasing all campus societies, past achievements, and annual project highlights to incoming first-year students.',
       'Seminar', 
       'Central University Auditorium', 
       DATE_SUB(CURDATE(), INTERVAL 20 DAY), 
       '10:00:00', 
       '13:00:00', 
       DATE_SUB(NOW(), INTERVAL 25 DAY), 
       250, 
       NULL
FROM clubs WHERE name = 'Cultural Society';

-- -----------------------------------------------------------------------------
-- 4. Seed Registrations for Event 6 to demonstrate FULL status
-- -----------------------------------------------------------------------------
INSERT INTO event_registrations (student_id, event_id, status)
SELECT s.id, e.id, 'REGISTERED'
FROM students s, events e
WHERE s.email = 'aarav.sharma@example.test' 
  AND e.title = 'Elite Drone Piloting Masterclass';

INSERT INTO event_registrations (student_id, event_id, status)
SELECT s.id, e.id, 'REGISTERED'
FROM students s, events e
WHERE s.email = 'diya.patel@example.test' 
  AND e.title = 'Elite Drone Piloting Masterclass';
