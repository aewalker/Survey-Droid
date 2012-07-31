CREATE DATABASE survey_droid;

USE survey_droid;

CREATE TABLE `sd_answers` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` int(10) unsigned NOT NULL,
  `subject_id` int(10) unsigned NOT NULL,
  `ans_type` tinyint(4) NOT NULL,
  `ans_text` text,
  `ans_value` int(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `answers_question_id_index` (`question_id`),
  KEY `answers_subject_id_index` (`subject_id`),
  KEY `answers_created_index` (`created`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_answers_choices` (
  `answer_id` int(10) unsigned NOT NULL,
  `choice_id` int(10) unsigned NOT NULL,
  KEY `answers_choices_answer_id_index` (`answer_id`),
  KEY `answers_choices_choice_id_index` (`choice_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_branches` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` int(10) unsigned NOT NULL,
  `next_q` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `branches_question_id_index` (`question_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_calls` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `contact_id` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `type` tinyint(4) NOT NULL,
  `duration` smallint(5) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `calls_subject_id_index` (`subject_id`),
  KEY `calls_created_index` (`created`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_choices` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `choice_type` tinyint(4) NOT NULL,
  `choice_text` varchar(255) DEFAULT NULL,
  `choice_img` text,
  `question_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `choices_question_id_index` (`question_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_commissions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `study_id` int(10) unsigned NOT NULL,
  `is_creator` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_conditions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `branch_id` int(10) unsigned NOT NULL,
  `question_id` int(10) unsigned NOT NULL,
  `choice_id` int(10) unsigned NOT NULL,
  `type` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `conditions_branch_id_index` (`branch_id`),
  KEY `conditions_question_id_index` (`question_id`),
  KEY `conditions_choice_id_index` (`choice_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_configurations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `study_id` int(10) unsigned NOT NULL,
  `c_key` text NOT NULL,
  `opt` tinytext NOT NULL,
  `c_value` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `configurations_study_id_index` (`study_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_extras` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `type` tinyint(4) NOT NULL,
  `data` longtext,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `extras_subject_id_index` (`subject_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_locations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `created` datetime NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  `accuracy` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `locations_created_index` (`created`),
  KEY `locations_subject_id_index` (`subject_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_questions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `survey_id` int(10) unsigned NOT NULL,
  `q_type` tinyint(4) NOT NULL,
  `q_text` text NOT NULL,
  `q_img_low` text,
  `q_img_high` text,
  `q_text_low` text,
  `q_text_high` text,
  PRIMARY KEY (`id`),
  KEY `questions_survey_id_index` (`survey_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_status_changes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `created` datetime NOT NULL,
  `status` tinyint(4) NOT NULL,
  `feature` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `status_changes_subject_id_index` (`subject_id`),
  KEY `status_changes_created_index` (`created`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_studies` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `start` datetime,
  `end` datetime,
  PRIMARY KEY (`id`),
  KEY `studies_id_index` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_subjects` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `phone_num` varchar(13) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `is_inactive` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_surveys` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `question_id` int(10) unsigned NOT NULL,
  `mo` varchar(255) DEFAULT NULL,
  `tu` varchar(255) DEFAULT NULL,
  `we` varchar(255) DEFAULT NULL,
  `th` varchar(255) DEFAULT NULL,
  `fr` varchar(255) DEFAULT NULL,
  `sa` varchar(255) DEFAULT NULL,
  `su` varchar(255) DEFAULT NULL,
  `subject_init` tinyint(4) DEFAULT '0',
  `subject_variables` text,
  `new_calls` tinyint(4) DEFAULT '0',
  `old_calls` tinyint(4) DEFAULT '0',
  `new_texts` tinyint(4) DEFAULT '0',
  `old_texts` tinyint(4) DEFAULT '0',
  `study_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `surveys_created_index` (`created`),
  KEY `surveys_study_id_index` (`study_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_surveys_taken` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `survey_id` int(10) unsigned NOT NULL,
  `status` tinyint(4) NOT NULL,
  `created` datetime NOT NULL,
  `rate` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `surveys_taken_subject_id_index` (`subject_id`),
  KEY `surveys_taken_survey_id_index` (`survey_id`),
  KEY `surveys_taken_created_index` (`created`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `sd_users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `email` varchar(320) NOT NULL,
  `password` char(41) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
