# Homework 2

Here are your tasks.

- ## Install MySQL: 
Click here to go to the "Installing and Upgrading MySQL" page. Then click on the link for your operating system. I installed in an Ubuntu VM and it went like this:

	- I clicked on "2.5 Installing MySQL on Linux"
	- I clicked on "2.5.1 Installing MySQL on Linux Using the MySQL Yum Repository", but it didn't look like there was a Yum repo for Ubuntu, so I went back.
	- So I clicked on "2.5.3 Installing MySQL on Linux Using the MySQL APT Repository" and it listed Ubuntu.
	- I was directed to "A Quick Guide to Using the MySQL APT Repository", so I went there.
	- "Steps for a Fresh Installation of MySQL" looked promising, so I clicked on that
	- I wound up on a page where I was able to download the APT repo, and it looked like I needed an Oracle Web account. But if you scroll down you'll see a link that says "No thanks, just start my download", so I did that.
	- The result was a .deb file, and the instructions gave me a dpkg command to run, which I did.
	- That popped up a window that asked what I wanted to install, so I just went with the defaults and chose "Ok".
  	- I kept entering commands as suggested by the installation process, which downloaded and installed a bunch of stuff.
	- Then I was asked for a root password for MySQL.
	- To check the install, run sudo service mysql status. You should see something about MySQL being active and/or running.

- ## Create a MySQL user for yourself:

	- Run mysql --user root --password at the command line, enter the root password for MySQL that you created, and run the following queries
	- CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';
	- GRANT ALL PRIVILEGES ON *.* TO 'username'@'localhost' WITH GRANT OPTION;
	- QUIT;
  	- Then run mysql -p and enter the password you just specified.
	- Try SHOW DATABASES; and you should see a few system DBs listed.
	- Install the Retailer sample database: Go here for instructions on getting the sample database. The result is a .zip file that, when extracted, gives you a .sql file named mysqlsampledatabase.sql, which is nothing more than a series of SQL commands/queries. To run it, do this: mysql -u username < mysqlsampledatabase.sql. As usual, you'll have to enter your password

To check that everything worked, do SHOW DATABASES in mysql and if you see one named classicmodels, then all is well. The web page that contains the link for the sample database has information on the tables and their fields.

Write each of the following queries: For each query, turn in the query and the result of running it on the Retail database that you just created.

- Count the number of employees whose last name or first name starts with the letter 'P'.
- For how many letters of the alphabet are there more than one employee whose last name starts with that letter? Hint: The substr function will be useful here in a GROUP BY clause.
- How many orders have not yet shipped?
- How many orders where shipped less than 2 days before they were required?
- For each distinct product line, what is the total dollar value of orders placed?
- For the first three customers in alphabetal order by name, what is the name of every product they have ordered?
- Install the python connector for MySQL: In this part of the homework you'll get experience running queries from python code and write a simple program to extract the structure of a MySQL database.

- ## Go here to download the python connector:
	- I chose "4.2 Installing Connector/Python from a Binary Distribution" and then looked under "Installing Connector/Python on Linux Using a Debian Package" since I'm on Ubuntu.
	- I wound up on the Download Connector/Python page where I chose my operating system and downloaded the .deb file, then I resumed following the directions, which involved running a dpkg command on the file I just downloaded.
	- Then you can test the installation by running python and trying import mysql.connector. If that does not produce an error, you're good to go.
	- Your task is to write a python program that takes a single command line argument, which is the name of a database, and prints the names of all of the tables in that database along with the number of rows in each table. Read through the documentation on the python connector here to see how to create a connection, issue a query, and walk over the results. For this exercise you'll submit your python code, which should all be in one file, along with the output of running your program on the sample database you installed earlier. Hint: The SHOW TABLES query will be useful here.

- Put all elements of the homework into a single file and submit it via Slack to the TA by the due date/time.
