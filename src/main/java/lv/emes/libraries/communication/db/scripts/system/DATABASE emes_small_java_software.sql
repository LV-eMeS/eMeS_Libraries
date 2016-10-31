#Drop JavaNote database if it exists.
#If database does not exist (first time initialization), the warning may come out,
#which can be ignored.
#DROP DATABASE IF EXISTS emes_small_java_software;

#Create database
CREATE DATABASE emes_small_java_software;
USE emes_small_java_software;

# Create Users table and insert 'default' user
CREATE TABLE software (
  Id INT NOT NULL AUTO_INCREMENT,
  Name TINYTEXT NOT NULL,
  Short_Name TINYTEXT,
  description TINYTEXT,
  download_link TINYTEXT,
  version TINYTEXT,
  binary_data LONGBLOB,
  PRIMARY KEY (Id)
);

#INSERT INTO Users VALUES(null, 'default', '1');