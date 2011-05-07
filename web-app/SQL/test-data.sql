/* Inserts some dummy data just to make sure everything works */

USE socioapp;

INSERT INTO users (id, username, email, password, first_name, last_name) VALUES
	(1, 'johnd', 'john.doe@example.com', 'b499bd98fcddc9ea3ce0cecf4521e9f7a9d21f05', 'John', 'Doe'),
	(2, 'janed', 'jane.doe@example.com', '0dca80e58ff7df07bf7d8847a8b6b598fbd9df6f', 'Jane', 'Doe');

INSERT INTO subjects (id, phone_num, first_name, last_name, device_id) VALUES
	(1, '0521035555', 'Graham', 'Chapman', 'phone1'),
	(2, '1936035555', 'Eric', 'Idle', 'phone2'),
	(3, '1234565555', 'Terry', 'Gilliam', 'phone3'),
	(4, '2905345555', 'Terry', 'Jones', 'phone4'),
	(5, '2936025555', 'John', 'Cleese', 'phone5'),
	(6, '2936905555', 'Michael', 'Palin', 'phone6');
	
INSERT INTO contacts (id, subject_id) VALUES
	(21, 2), (1, 4), (2, 3),
	(3, 5), (4, 6), (5, 1),
	(6, 4), (7, 1), (8, 4),
	(9, 4), (10, 3), (11, 6),
	(12, 5), (13, 3), (14, 6),
	(15, 5), (16, 2), (17, 2),
	(18, 5), (19, 4), (20, 1);
	
INSERT INTO surveys (id, name, question_id) VALUES
	(1, 'The Life of Brian', 1),
	(2, 'The Meaming of Life', 3);
	
INSERT INTO questions (id, survey_id, q_text) VALUES
	(1, 1, 'Shall we follow the gord?'),
	(2, 1, 'What side of life do you always look on?'),
	(3, 2, 'Another mint?'),
	(4, 2, 'What is the meaning of life?');
	
INSERT INTO branches (id, question_id, next_q) VALUES
	(1, 1, 2),
	(2, 3, 4);
	
INSERT INTO choices (id, choice_text, question_id) VALUES
	(1, 'Yes!', 1),
	(2, 'No!', 1),
	(3, 'Yes!', 3),
	(4, 'No!', 3);
