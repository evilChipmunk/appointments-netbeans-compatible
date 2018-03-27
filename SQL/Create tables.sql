use U05bhH;

 
  DROP TABLE IF EXISTS reminder ;
  DROP TABLE IF EXISTS  incrementType ;
  DROP TABLE IF EXISTS  appointment ;
 DROP TABLE IF EXISTS  customer ;
  DROP TABLE IF EXISTS  address ;
 DROP TABLE IF EXISTS  city ;
  DROP TABLE IF EXISTS  country ;
 DROP TABLE IF EXISTS  user;

CREATE TABLE user
( 
     UserId INT PRIMARY KEY,
	 UserName varchar(50) UNIQUE,
	 Password varchar(50),
	 Active bit,
	 CreateBy varchar(50),
	 CreateDate datetime,
	 LastUpdatedBy varchar(50),
	 LastUpdate timestamp 
)
;

CREATE TABLE country
( 
     CountryId INT PRIMARY KEY ,
	 Country varchar(50), 
	 CreatedBy varchar(50),
	 CreateDate datetime,
	 LastUpdateBy varchar(50),
	 LastUpdate timestamp 
)
;

CREATE TABLE city
(  
     CityId INT PRIMARY KEY,
	 City varchar(50), 
	 CountryId INT,
	 CreatedBy varchar(50),
	 CreateDate datetime,
	 LastUpdateBy varchar(50),
	 LastUpdate timestamp 
)
;

CREATE TABLE address
(  
     AddressId INT PRIMARY KEY,
	 Address varchar(50), 
	 Address2 varchar(50),
	 CityId INT,
	 PostalCode varchar(10),
	 Phone varchar(20),
	 CreatedBy varchar(50),
	 CreateDate datetime,
	 LastUpdateBy varchar(50),
	 LastUpdate timestamp 
)
;

CREATE TABLE customer
(  
     CustomerId INT PRIMARY KEY,
	 CustomerName varchar(50), 
	 AddressId INT,
	 Active bit,
	 CreatedBy varchar(50),
	 CreateDate datetime,
	 LastUpdateBy varchar(50),
	 LastUpdate timestamp 
)
;

CREATE TABLE appointment
(  
     AppointmentId INT PRIMARY KEY,
	 CustomerId INT,
	 Title varchar(255), 
	 Description TEXT,
	 Location TEXT,
	 Contact TEXT,
	 URL varchar(255),
	 Start datetime,
	 End datetime,
	 CreatedBy varchar(50),
	 CreateDate datetime,
	 LastUpdateBy varchar(50),
	 LastUpdate timestamp 
)
;

CREATE TABLE incrementType
(
	Id VARCHAR(64) NOT NULL UNIQUE,
    IncrementTypeId INT PRIMARY KEY,
	Description varchar(45)
)
;


CREATE TABLE reminder
( 
     ReminderId INT PRIMARY KEY,
	 ReminderDate datetime,
	 SnoozeIncrement int,
	 IncrementTypeId INT,
	 AppointmentId INT, 
	 CreatedBy varchar(50),
	 CreateDate datetime,
	 LastUpdateBy varchar(50),
	 LastUpdate timestamp 
)
;
 
 
 
ALTER TABLE address MODIFY COLUMN addressid INT auto_increment;
ALTER TABLE appointment MODIFY COLUMN appointmentid INT auto_increment;
ALTER TABLE city MODIFY COLUMN cityid INT auto_increment;
ALTER TABLE country MODIFY COLUMN countryid INT auto_increment;
ALTER TABLE customer MODIFY COLUMN customerid INT auto_increment;
ALTER TABLE reminder MODIFY COLUMN reminderid INT auto_increment;
ALTER TABLE user MODIFY COLUMN userid INT auto_increment;

ALTER TABLE city ADD CONSTRAINT FK_City_Country FOREIGN KEY(CountryId) REFERENCES country(CountryId);
ALTER TABLE address ADD CONSTRAINT FK_Address_City FOREIGN KEY(CityId) REFERENCES city(CityId);
ALTER TABLE customer ADD CONSTRAINT FK_Customer_Address FOREIGN KEY(AddressId) REFERENCES address (AddressId);
ALTER TABLE appointment ADD CONSTRAINT FK_Appointment_Customer FOREIGN KEY(CustomerId) REFERENCES customer(CustomerId);
ALTER TABLE reminder ADD CONSTRAINT FK_Reminder_IncrementType FOREIGN KEY(IncrementTypeId) REFERENCES incrementType(IncrementTypeId); 
ALTER TABLE reminder ADD CONSTRAINT FK_Reminderity_Appointment FOREIGN KEY(AppointmentId) REFERENCES appointment(AppointmentId);
 
 