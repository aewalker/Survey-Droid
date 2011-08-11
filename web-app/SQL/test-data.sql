-- MySQL dump 10.13  Distrib 5.1.49, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: peoples
-- ------------------------------------------------------
-- Server version	5.1.49-1ubuntu8.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `answers`
--

DROP TABLE IF EXISTS `answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `answers` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` int(10) unsigned NOT NULL,
  `subject_id` int(10) unsigned NOT NULL,
  `ans_type` tinyint(4) NOT NULL,
  `ans_text` text,
  `ans_value` int(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=73 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answers`
--

LOCK TABLES `answers` WRITE;
/*!40000 ALTER TABLE `answers` DISABLE KEYS */;
INSERT INTO `answers` VALUES (1,1,4,0,NULL,NULL,'2011-07-28 17:38:15'),(2,3,4,0,NULL,NULL,'2011-07-28 17:38:17'),(3,2,4,0,NULL,50,'2011-07-28 17:38:17'),(4,4,4,0,NULL,NULL,'2011-07-28 17:38:18'),(5,1,4,0,NULL,NULL,'2011-07-28 17:38:15'),(6,3,4,0,NULL,NULL,'2011-07-28 17:38:17'),(7,2,4,0,NULL,50,'2011-07-28 17:38:17'),(8,4,4,0,NULL,NULL,'2011-07-28 17:38:18'),(9,1,2,0,NULL,NULL,'2011-07-28 20:12:22'),(10,2,2,0,NULL,50,'2011-07-28 20:12:24'),(11,3,2,0,NULL,NULL,'2011-07-28 20:12:26'),(12,4,2,0,NULL,NULL,'2011-07-28 20:12:27'),(13,1,2,0,NULL,NULL,'2011-07-29 14:40:25'),(14,2,2,0,NULL,50,'2011-07-29 14:40:31'),(15,3,2,0,NULL,NULL,'2011-07-29 14:40:34'),(16,4,2,0,NULL,NULL,'2011-07-29 14:40:36'),(17,1,2,0,NULL,NULL,'2011-07-30 03:52:17'),(18,2,2,0,NULL,50,'2011-07-30 03:52:20'),(19,3,2,0,NULL,NULL,'2011-07-30 03:52:22'),(20,4,2,0,NULL,NULL,'2011-07-30 03:52:23'),(21,1,2,0,NULL,NULL,'2011-08-01 19:21:43'),(22,2,2,0,NULL,50,'2011-08-01 19:21:49'),(23,8,2,0,NULL,50,'2011-08-01 19:21:53'),(24,1,2,0,NULL,NULL,'2011-08-02 00:24:42'),(25,2,2,0,NULL,NULL,'2011-08-02 00:24:47'),(26,8,2,0,NULL,NULL,'2011-08-02 00:24:52'),(27,1,4,0,NULL,NULL,'2011-07-28 20:05:32'),(28,2,4,0,NULL,NULL,'2011-07-28 20:05:35'),(29,3,4,0,NULL,NULL,'2011-07-28 20:05:38'),(30,4,4,0,NULL,NULL,'2011-07-28 20:05:39'),(31,1,4,0,NULL,NULL,'2011-08-01 17:49:14'),(32,2,4,0,NULL,NULL,'2011-08-01 17:49:16'),(33,8,4,0,NULL,NULL,'2011-08-01 17:49:19'),(34,1,4,0,NULL,NULL,'2011-08-02 16:13:32'),(35,2,4,0,NULL,NULL,'2011-08-02 16:13:41'),(36,8,4,0,NULL,NULL,'2011-08-02 16:13:45'),(37,1,4,0,NULL,NULL,'2011-08-03 19:19:17'),(38,2,4,0,NULL,NULL,'2011-08-03 19:19:21'),(39,8,4,0,NULL,NULL,'2011-08-03 19:19:23'),(40,1,4,0,NULL,NULL,'2011-08-03 19:42:45'),(41,2,4,0,NULL,NULL,'2011-08-03 19:42:49'),(42,8,4,0,NULL,NULL,'2011-08-03 19:42:51'),(43,1,4,0,NULL,NULL,'2011-08-03 20:13:13'),(44,2,4,0,NULL,NULL,'2011-08-03 20:13:34'),(45,8,4,0,NULL,NULL,'2011-08-03 20:14:58'),(46,1,4,0,NULL,NULL,'2011-08-03 20:19:47'),(47,2,4,0,NULL,NULL,'2011-08-03 20:19:59'),(48,8,4,0,NULL,NULL,'2011-08-03 20:20:06'),(49,1,4,0,NULL,NULL,'2011-08-03 20:37:54'),(50,2,4,0,NULL,NULL,'2011-08-03 20:37:55'),(51,8,4,0,NULL,NULL,'2011-08-03 20:37:57'),(52,1,4,0,NULL,NULL,'2011-08-03 20:40:27'),(53,8,4,0,NULL,NULL,'2011-08-03 20:40:28'),(54,2,4,0,NULL,NULL,'2011-08-03 20:40:28'),(55,9,2,0,NULL,NULL,'2011-08-10 17:49:57'),(56,11,2,0,NULL,NULL,'2011-08-10 17:50:07'),(57,25,2,0,NULL,NULL,'2011-08-10 17:50:34'),(58,12,2,0,NULL,NULL,'2011-08-10 17:50:39'),(59,13,2,0,NULL,NULL,'2011-08-10 17:50:42'),(60,14,2,0,NULL,NULL,'2011-08-10 17:50:48'),(61,15,2,0,NULL,NULL,'2011-08-10 17:50:52'),(62,16,2,0,NULL,NULL,'2011-08-10 17:50:57'),(63,26,2,0,NULL,NULL,'2011-08-10 17:51:18'),(64,17,2,0,NULL,NULL,'2011-08-10 17:51:28'),(65,18,2,0,NULL,NULL,'2011-08-10 17:51:31'),(66,19,2,0,NULL,NULL,'2011-08-10 17:51:36'),(67,20,2,0,NULL,NULL,'2011-08-10 17:51:40'),(68,21,2,0,NULL,NULL,'2011-08-10 17:51:47'),(69,22,2,0,NULL,NULL,'2011-08-10 17:51:51'),(70,23,2,0,NULL,NULL,'2011-08-10 17:51:53'),(71,29,2,0,NULL,NULL,'2011-08-10 17:51:56'),(72,24,2,0,NULL,NULL,'2011-08-10 17:52:00');
/*!40000 ALTER TABLE `answers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `answers_choices`
--

DROP TABLE IF EXISTS `answers_choices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `answers_choices` (
  `answer_id` int(10) unsigned NOT NULL,
  `choice_id` int(10) unsigned NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answers_choices`
--

LOCK TABLES `answers_choices` WRITE;
/*!40000 ALTER TABLE `answers_choices` DISABLE KEYS */;
INSERT INTO `answers_choices` VALUES (1,0),(2,0),(3,0),(4,0),(5,0),(6,0),(7,0),(8,0),(9,0),(10,0),(11,0),(12,0),(13,0),(14,0),(15,0),(16,0),(17,0),(18,0),(19,0),(20,0),(21,1),(22,0),(23,0),(24,1),(25,0),(26,0),(27,0),(28,0),(29,0),(30,0),(31,1),(32,0),(33,0),(34,2),(35,0),(36,0),(37,1),(38,0),(39,0),(40,1),(41,0),(42,0),(43,2),(44,0),(45,0),(46,1),(47,0),(48,0),(49,1),(50,0),(51,0),(52,2),(53,0),(54,0),(55,3),(56,14),(57,0),(58,16),(59,18),(60,22),(61,27),(62,33),(63,0),(64,39),(65,42),(66,0),(67,0),(68,0),(69,0),(70,0),(71,0),(72,44);
/*!40000 ALTER TABLE `answers_choices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `branches`
--

DROP TABLE IF EXISTS `branches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `branches` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` int(10) unsigned NOT NULL,
  `next_q` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=50 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branches`
--

LOCK TABLES `branches` WRITE;
/*!40000 ALTER TABLE `branches` DISABLE KEYS */;
INSERT INTO `branches` VALUES (1,1,2),(2,2,8),(3,9,10),(4,9,11),(5,10,11),(6,11,12),(7,11,25),(8,12,13),(9,13,14),(10,14,15),(11,15,16),(12,16,17),(13,16,26),(14,17,20),(15,17,27),(16,17,18),(17,17,28),(18,18,28),(19,18,19),(20,19,20),(21,20,21),(22,21,22),(23,22,23),(24,23,29),(25,25,12),(26,26,17),(27,27,19),(28,28,20),(29,29,24),(30,30,31),(31,30,40),(39,32,36),(38,31,41),(37,31,32),(36,31,36),(40,32,33),(41,32,35),(42,33,34),(43,34,36),(44,35,34),(45,36,37),(46,37,38),(47,38,39),(48,40,31),(49,41,36);
/*!40000 ALTER TABLE `branches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `calls`
--

DROP TABLE IF EXISTS `calls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calls` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `contact_id` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `type` tinyint(4) NOT NULL,
  `duration` smallint(5) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calls`
--

LOCK TABLES `calls` WRITE;
/*!40000 ALTER TABLE `calls` DISABLE KEYS */;
/*!40000 ALTER TABLE `calls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `choices`
--

DROP TABLE IF EXISTS `choices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `choices` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `choice_type` tinyint(4) NOT NULL,
  `choice_text` varchar(255) DEFAULT NULL,
  `choice_img` text,
  `question_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=78 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `choices`
--

LOCK TABLES `choices` WRITE;
/*!40000 ALTER TABLE `choices` DISABLE KEYS */;
INSERT INTO `choices` VALUES (1,0,'Yes',NULL,1),(2,0,'No',NULL,1),(3,0,'Yes',NULL,9),(4,0,'No',NULL,9),(5,0,'Within the last hour',NULL,10),(6,0,'1 to 2 hours ago',NULL,10),(7,0,'3 to 5 hours ago',NULL,10),(8,0,'More than 5 hours ago',NULL,10),(9,0,'Retail/store',NULL,11),(10,0,'Construction',NULL,11),(11,0,'Delivery/driver',NULL,11),(12,0,'Restaurant',NULL,11),(13,0,'Factory',NULL,11),(14,0,'Other',NULL,11),(15,0,'Full-time',NULL,12),(16,0,'Part-time',NULL,12),(17,0,'I don\'t know',NULL,12),(18,0,'Temporary/short-term',NULL,13),(19,0,'Permanent/long-term',NULL,13),(20,0,'I don\'t know',NULL,13),(21,0,'Informal/off the books',NULL,14),(22,0,'Formal/on the books',NULL,14),(23,0,'I don\'t know',NULL,14),(24,0,'Less than $7.25',NULL,15),(25,0,'$7.25 to $12.00',NULL,15),(26,0,'$12.00 to $15.00',NULL,15),(27,0,'More than $15.00',NULL,15),(28,0,'I don\'t know',NULL,15),(29,0,'Newark',NULL,16),(30,0,'Kearny',NULL,16),(31,0,'East Orange',NULL,16),(32,0,'Elizabeth',NULL,16),(33,0,'Other location',NULL,16),(34,0,'Looked in newspaper or Internet',NULL,17),(35,0,'Walk-in',NULL,17),(36,0,'Job search or temp agency',NULL,17),(37,0,'Referral from parole, Kintock, or other caseworker',NULL,17),(38,0,'Contacted prior employer',NULL,17),(39,0,'From friend or family',NULL,17),(40,0,'Other ',NULL,17),(41,0,'List names given in initial interview',NULL,18),(42,0,'Other person',NULL,18),(43,0,'Yes',NULL,24),(44,0,'No',NULL,24),(45,0,'I don\'t remember',NULL,24),(46,0,'Home',NULL,30),(47,0,'Work',NULL,30),(48,0,'Friend or family\'s home',NULL,30),(49,0,'Car/bus/train',NULL,30),(50,0,'Outside',NULL,30),(51,0,'Kintock or parole',NULL,30),(52,0,'Other location',NULL,30),(53,0,'Eating',NULL,31),(54,0,'Doing errands/shopping',NULL,31),(55,0,'Hanging out/watching TV',NULL,31),(56,0,'Smoking a cigarette',NULL,31),(57,0,'Drinking/using other substances',NULL,31),(58,0,'Working',NULL,31),(59,0,'Household chores',NULL,31),(60,0,'Searching for work',NULL,31),(61,0,'Other',NULL,31),(62,0,'Looking in the newspaper/Internet/job ads',NULL,32),(63,0,'Filling out an application/walk-in',NULL,32),(64,0,'At a job search or temp agency',NULL,32),(65,0,'Following up on a referral from parole, Kintock, or caseworker',NULL,32),(66,0,'Contacting prior employer',NULL,32),(67,0,'Following up on a referral from a friend or family member',NULL,32),(68,0,'Talking to friends or family about jobs',NULL,32),(69,0,'List names given in initial interview',NULL,33),(70,0,'Other person',NULL,33),(71,0,'List names given in initial interview',NULL,35),(72,0,'Other person',NULL,35),(73,0,'By myself',NULL,36),(74,0,'With friends or family',NULL,36),(75,0,'With strangers',NULL,36),(76,0,'With coworkers',NULL,36),(77,0,'With others',NULL,36);
/*!40000 ALTER TABLE `choices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conditions`
--

DROP TABLE IF EXISTS `conditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conditions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `branch_id` int(10) unsigned NOT NULL,
  `question_id` int(10) unsigned NOT NULL,
  `choice_id` int(10) unsigned NOT NULL,
  `type` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conditions`
--

LOCK TABLES `conditions` WRITE;
/*!40000 ALTER TABLE `conditions` DISABLE KEYS */;
INSERT INTO `conditions` VALUES (1,3,9,4,0),(2,4,9,3,0),(3,6,11,9,0),(4,6,11,10,0),(5,6,11,11,0),(6,6,11,12,0),(7,6,11,13,0),(8,7,11,14,0),(9,12,16,29,0),(10,12,16,30,0),(11,12,16,31,0),(12,12,16,32,0),(13,13,16,33,0),(14,14,17,34,0),(15,14,17,35,0),(16,14,17,36,0),(17,14,17,37,0),(18,15,17,38,0),(19,16,17,39,0),(20,17,17,40,0),(21,18,18,41,0),(22,19,18,42,0),(23,31,30,52,0),(24,33,60,67,0),(25,34,60,68,0),(26,35,60,61,0),(27,37,31,60,0),(28,38,31,61,0),(29,40,32,67,0),(30,41,32,68,0);
/*!40000 ALTER TABLE `conditions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configurations`
--

DROP TABLE IF EXISTS `configurations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configurations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `opt` tinytext NOT NULL,
  `c_key` text NOT NULL,
  `c_value` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=135 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configurations`
--

LOCK TABLES `configurations` WRITE;
/*!40000 ALTER TABLE `configurations` DISABLE KEYS */;
INSERT INTO `configurations` VALUES (132,'==','time_tracked.0.end','2200'),(131,'==','time_tracked.0.start','9000'),(124,'==','location_tracked.0.radius','10000'),(123,'==','location_tracked.0.lat','-87.627778'),(122,'==','location_tracked.0.long','41.881944'),(115,'==','features_enabled.location','1'),(114,'==','features_enabled.callog','1'),(113,'==','features_enabled.survey','1'),(112,'==','voice_format','mpeg4'),(111,'==','show_survey_name','1'),(110,'==','allow_no_choices','0'),(109,'==','allow_blank_free_response','1'),(108,'==','admin_name','Austin'),(107,'==','admin_phone_number','7652996509'),(106,'==','location_interval','10'),(105,'==','device_enabled','1'),(104,'==','server','50.19.254.168'),(103,'==','pull_interval','1440'),(102,'==','push_interval','1440'),(100,'==','https','1'),(101,'==','scheduler_interval','1440');
/*!40000 ALTER TABLE `configurations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locations`
--

DROP TABLE IF EXISTS `locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `locations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `created` datetime NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locations`
--

LOCK TABLES `locations` WRITE;
/*!40000 ALTER TABLE `locations` DISABLE KEYS */;
/*!40000 ALTER TABLE `locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `questions` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `survey_id` int(10) unsigned NOT NULL,
  `q_type` tinyint(4) NOT NULL,
  `q_text` text NOT NULL,
  `q_img_low` text,
  `q_img_high` text,
  `q_text_low` text,
  `q_text_high` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,1,0,'Did you go to Chicago today?','','','',''),(2,1,2,'How was it?',NULL,NULL,'Terrible','Awesome'),(8,1,2,'What\'s for dinner?','','','Pizza','Ice Cream'),(9,2,0,'Did you have a job interview within the last 30 minutes?','','','',''),(10,2,0,'When did you have the interview?','','','',''),(11,2,0,'What type of job did you interview for?','','','',''),(12,2,0,'Is the job full-time or part-time?','','','',''),(13,2,0,'Is the job temporary or permanent?','','','',''),(14,2,0,'Is the job informal or formal work?','','','',''),(15,2,0,'How much would the job pay PER HOUR?','','','',''),(16,2,0,'Where is the job located?','','','',''),(17,2,1,'How did you find out about the job?','','','',''),(18,2,0,'Who?','','','',''),(19,2,2,'How close are you to this person?','','','Not at all close','Extremely close'),(20,2,2,'How happy did you feel about the interview?','','','Not at all happy','Extremely happy'),(21,2,2,'How sad did you feel about the interview?','','','Not at all sad','Extremely sad'),(22,2,2,'How stressed did you feel about the interview?','','','Not at all stressed','Extremely stressed'),(23,2,2,'Do you think you\'ll receive a job offer for this job?','','','Definitely No','Definitely Yes'),(24,2,0,'Did you talk about your criminal record?','','','',''),(25,2,4,'Please specify type of job:','','','',''),(26,2,4,'Please specify location of job:','','','',''),(27,2,0,'Did you interview with your prior employer?','','','',''),(28,2,4,'Please specify how you found the job:','','','',''),(29,2,2,'Do you think the employer knew you have a criminal record?','','','Definitely No','Definitely Yes'),(30,3,0,'Where are you right now?','','','',''),(31,3,1,'What are you doing right now? (check all that apply)','','','',''),(32,3,1,'How are you searching right now? (check all that apply)','','','',''),(33,3,0,'Who gave you the referral?','','','',''),(34,3,2,'How close are you to this person?','','','Not at all close','Extremely close'),(35,3,1,'Who are you talking to about jobs? (check all that apply)','','','',''),(36,3,0,'Right now, are you by yourself or with others?','','','',''),(37,3,2,'Right now, how happy do you feel?','','','Not at all happy','Extremely happy'),(38,3,2,'Right now, how sad do you feel?','','','Not at all sad','Extremely sad'),(39,3,2,'Right now, how stressed do you feel?','','','Not at all stressed','Extremely stressed'),(40,3,4,'Please specify where you are:','','','',''),(41,3,4,'Please specify what you\'re doing right now:','','','','');
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_changes`
--

DROP TABLE IF EXISTS `status_changes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `status_changes` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject_id` int(10) unsigned NOT NULL,
  `created` datetime NOT NULL,
  `status` tinyint(4) NOT NULL,
  `feature` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_changes`
--

LOCK TABLES `status_changes` WRITE;
/*!40000 ALTER TABLE `status_changes` DISABLE KEYS */;
/*!40000 ALTER TABLE `status_changes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subjects` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `phone_num` varchar(13) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (5,'6467344900','Naomi','Sugie','A1000013744211'),(2,'6095339760','Austin','Walker','A0000024FDCC22'),(3,'6095339761','Tony','Xaio','A0000024FDD4E0'),(4,'5555555555','Emulator','Device','000000000000000');
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surveys`
--

DROP TABLE IF EXISTS `surveys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `surveys` (
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
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveys`
--

LOCK TABLES `surveys` WRITE;
/*!40000 ALTER TABLE `surveys` DISABLE KEYS */;
INSERT INTO `surveys` VALUES (1,'Testing Survey','2011-07-10 20:19:39',1,'1200,1400,1600','1200,1400,1600','1200,1400,1600','1200,1400,1600,0040,0041.0042','1200,1400,1600','1200,1400,1600','1200,1400,1600',1),(2,'Job Interview','2011-08-10 15:12:39',9,'','','','','','','',1),(3,'Random Time-Based','2011-08-10 16:20:30',30,'1109','1757','1449','1503','1128','906','1617',0);
/*!40000 ALTER TABLE `surveys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `email` varchar(320) NOT NULL,
  `password` char(41) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `admin` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (5,'naomi','nsugie@princeton.edu','02ab753619a0101ec7314a4587e5e2f8cf052fee','Naomi','Sugie',1),(4,'austin','awalkerenator@gmail.com','e353a11397a17ffb60763a431cd9c7cfca4c11b7','Austin','Walker',1),(6,'tonyx','tonyx.ca@gmail.com','cc1775a2bc29283bd676dd6bfce3a829a18ce479','Tony','Xiao',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-08-11  1:00:19
