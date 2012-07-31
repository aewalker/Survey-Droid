#!/bin/bash

####
## Survey Droid Server Installation Script
####
clear
echo "Welcome to the Survey Droid installation script. Before we begin the installation process, some information must be collected."
echo -n "First, do you have apache2 (with PHP support) and mysql installed [y/n]? "
read -e REQ_SOFTWARE_INSTALLED
if [ "$REQ_SOFTWARE_INSTALLED" != "y" ]; then
	echo "Please install apache2 and mysql before installing Survey Droid"
	exit
fi

echo "In order to create the Survey Droid database, this script needs a username and password with wich to log into mysql.  This user must be able to create a users and databases."
echo -n "Please enter mysql username: "
read -e MYSQL_USER
echo -n "Please enter password for $MYSQL_USER: "
read -s -e MYSQL_PASSWORD
echo -n "Atempting to access the database... "

DATABASES=`echo "show databases;" | mysql -u $MYSQL_USER -p${MYSQL_PASSWORD} 2>&1`
if `echo ${DATABASES} | grep "Access denied" 1>/dev/null 2>&1`; then
	echo "Access was denied with the given username and password; please try again"
	exit
fi
if `echo ${DATABASES} | grep "survey_droid" 1>/dev/null 2>&1`; then
	echo "It looks like you already have a database called survey_droid; please rename it or look into installing Survey Droid with a custom database name"
	exit
fi
echo "sucess! It looks like database setup will work."
echo "Finally, what password should be used for the Survey Droid database user?  You will need to remember this for later"
echo -n "Survey Droid MYSQL user password? "
read -s -e SD_MYSQL_USER_PASS
echo ""

echo -n "Ready to install.  Press ENTER to continue."
read -e 
echo ""

echo "Installing now..."
echo -n "...setting up sd_user in mysql... "
RESULT=`echo "CREATE USER 'sd_user'@'localhost' IDENTIFIED BY '${SD_MYSQL_USER_PASS}'" | mysql -u $MYSQL_USER -p${MYSQL_PASSWORD} 2>&1`
if [ -z "$RESULT" ]; then
	echo "done!"
else
	echo "failed!"
	echo "Aborting script"
	echo "mysql reported the following:"
	echo $RESULT
	exit
fi
echo -n "...creating database and tables... "
## Get into the right directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
RESULT=`echo "source ${SCRIPT_DIR}/setup.sql;" | mysql -u $MYSQL_USER -p${MYSQL_PASSWORD} 2>&1`
if [ -z "$RESULT" ]; then
	echo "done!"
else
	echo "failed!"
	echo "Aborting script"
	echo "mysql reported the following:"
	echo $RESULT
	exit
fi
#echo -n "...setting some default configuration settings... "
#RESULT=`echo "source ${SCRIPT_DIR}/set_config_defaults.sql;" | mysql -u $MYSQL_USER -p${MYSQL_PASSWORD} 2>&1`
#if [ -z "$RESULT" ]; then
#	echo "done!"
#else
#	echo "failed!"
#	echo "Aborting script"
#	echo "mysql reported the following:"
#	echo $RESULT
#	exit
#fi
echo -n "...granting permissions to the Survey Droid user... "
RESULT=`echo "GRANT SELECT, UPDATE, INSERT, DELETE ON survey_droid.* TO 'sd_user'@'localhost';" | mysql -u $MYSQL_USER -p${MYSQL_PASSWORD} 2>&1`
if [ -z "$RESULT" ]; then
	echo "done!"
else
	echo "failed!"
	echo "Aborting script"
	echo "mysql reported the following:"
	echo $RESULT
	exit
fi
echo -n "...atempting to log in as the newly created user... "
RESULT=`echo "" | mysql -u sd_user -p${SD_MYSQL_USER_PASS} survey_droid 2>&1`
if [ -z "$RESULT" ]; then
	echo "done!"
else
	echo "failed!"
	echo "The script finished installing but was unable to log in using the newly created user; something must have gone wrong"
	echo "mysql reported the following:"
	echo $RESULT
	exit
fi
echo ""
echo "Survey Droid database installation finished"
