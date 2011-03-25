/* Inserts some dummy data just to make sure everything works */

USE socioapp;

INSERT INTO researchers (id, email, pass, first_name, last_name) VALUES
	(1, 'john.doe@example.com', SHA1('johnpass'), 'John', 'Doe'),
	(2, 'jane.doe@example.com', SHA1('janepass'), 'Jane', 'Doe');

INSERT INTO subjects (id, phone_num, first_name, last_name) VALUES
	(1, '440521035555', 'Graham', 'Chapman'),
	(2, '441936035555', 'Eric', 'Idle'),
	(3, '441234565555', 'Terry', 'Gilliam'),
	(4, '442905345555', 'Terry', 'Jones'),
	(5, '442936025555', 'John', 'Cleese'),
	(6, '442936905555', 'Michael', 'Palin');
	
INSERT INTO contacts (id, subject_id) VALUES
	(21, 2), (1, 4), (2, 3),
	(3, 5), (4, 6), (5, 1),
	(6, 4), (7, 1), (8, 4),
	(9, 4), (10, 3), (11, 6),
	(12, 5), (13, 3), (14, 6),
	(15, 5), (16, 2), (17, 2),
	(18, 5), (19, 4), (20, 1);
	
INSERT INTO surveys (id, name) VALUES
	(1, 'The Life of Brian'),
	(2, 'The Meaming of Life');
	
INSERT INTO questions (id, survey_id, ques_type, text) VALUES
	(1, 1, 'choice', 'Shall we follow the gord?'),
	(2, 1, 'free_resp', 'What side of life do you always look on?'),
	(3, 2, 'choice', 'Another mint?'),
	(4, 2, 'free_resp', 'What is the meaning of life?');
	
INSERT INTO options (id, text) VALUES
	(1, 'Yes!'),
	(2, 'No!');
	
INSERT INTO questions_optionss (question_id, opt_id) VALUES
	(1, 1), (4, 1),
	(1, 2), (4, 2);
	
INSERT INTO location_actions (id, subject_id, created, longitude, lattitude) VALUES
	(1, 1, '1969-01-01 11:11:11', 51.511056, -0.224977),
	(2, 2, '1969-01-01 11:11:11', 51.511056, -0.224977),
	(3, 3, '1969-01-01 11:11:11', 51.511056, -0.224977),
	(4, 4, '1969-01-01 11:11:11', 51.511056, -0.224977),
	(5, 5, '1969-01-01 11:11:11', 51.511056, -0.224977),
	(6, 6, '1969-01-01 11:11:11', 51.511056, -0.224977);
	
INSERT INTO call_actions (id, subject_id, created, act_type, duration) VALUES
	(1, 1, '1969-01-01 11:11:11', 'call', 10),
	(2, 2, '1969-01-01 11:11:11', 'call', 10),
	(3, 3, '1969-01-01 11:11:11', 'call', 10),
	(4, 4, '1969-01-01 11:11:11', 'call', 10),
	(5, 5, '1969-01-01 11:11:11', 'call', 10),
	(6, 6, '1969-01-01 11:11:11', 'call', 10);
	
INSERT INTO status_changes (id, subject_id, created, status, feature) VALUES
	(1, 1, '1969-01-01 11:11:11', 'enabled', 'gps'),
	(2, 2, '1969-01-01 11:11:11', 'enabled', 'gps'),
	(3, 3, '1969-01-01 11:11:11', 'enabled', 'gps'),
	(4, 4, '1969-01-01 11:11:11', 'enabled', 'gps'),
	(5, 5, '1969-01-01 11:11:11', 'enabled', 'gps'),
	(6, 6, '1969-01-01 11:11:11', 'enabled', 'gps');
