USE U05bhH;

/*---------------------------------------USER-------------------------------------------*/

/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_DeleteUserById;
CREATE procedure sp_DeleteUserById
(
	IN id int
)

DELETE FROM user WHERE userId = id; 

/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP PROCEDURE IF EXISTS sp_SaveUser$$
CREATE PROCEDURE sp_SaveUser
( 
	 INOUT id INT,
	 IN name varchar(50),
	 IN password varchar(50),
	 IN active bit,
	 IN createdBy varchar(50),
	 IN createDate datetime,
	 IN lastUpdatedBy varchar(50),
	 IN lastUpdate timestamp 
)

BEGIN
INSERT INTO user
	(UserId, UserName, Password, Active, CreateBy, CreateDate, LastUpdatedBy, LastUpdate)
    VALUES (id, name, password, active, createdBy, createDate, lastUpdatedBy, lastUpdate)

    on duplicate key update 
    UserName = name,
    Password = password,
    Active = active, 
    LastUpdatedBy = lastUpdatedBy,
    LastUpdate = lastUpdate;
   
    if (id = 0) THEN BEGIN SET id = (SELECT LAST_INSERT_ID()); END; END IF;
     SELECT id;
END$$
DELIMITER ;
    
/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetUserById;
CREATE procedure sp_GetUserById
(
	IN id int
)
 
SELECT UserId as Id, UserName as Name, Password, Active, CreateBy as CreatedBy, CreateDate, LastUpdatedBy, LastUpdate
FROM user
WHERE UserId = id; 


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetUserByNameAndPassword;
CREATE procedure sp_GetUserByNameAndPassword
(
	IN name varchar(50),
    IN password varchar(50)
)

SELECT UserId as Id, UserName as Name, Password, Active, CreateBy as CreatedBy, CreateDate, LastUpdatedBy, LastUpdate
FROM user
WHERE UserName = name
AND Password = password; 

    
/*---------------------------------------COUNTRY-------------------------------------------*/
/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_SaveCountry$$
CREATE procedure sp_SaveCountry
(
	 INOUT id INT, 
	 IN name varchar(50), 
	 IN createdBy varchar(50),
	 IN createDate datetime,
	 IN lastUpdatedBy varchar(50),
	 IN lastUpdate timestamp 
)

BEGIN
INSERT INTO country
	(CountryId, Country, CreatedBy, CreateDate, LastUpdateBy, LastUpdate)
    VALUES (id, name, createdBy, createDate, lastUpdatedBy, lastUpdate)
    on duplicate key update 
    Country = name, 
    LastUpdateBy = lastUpdatedBy,
    LastUpdate = lastUpdate; 
    
    if (id = 0) THEN BEGIN SET id = (SELECT LAST_INSERT_ID()); END; END IF;
    SELECT id;
END$$
DELIMITER ;

/*-------------------------------------CREATE PROC------------------------------------------*/

DROP procedure IF EXISTS sp_DeleteCountryById;
CREATE procedure sp_DeleteCountryById
(
	IN id INT
)

DELETE FROM country WHERE CountryId = id; 

/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetCountryById;
CREATE procedure sp_GetCountryById
(
	 IN id INT
)

SELECT CountryId as Id, Country as Name, CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM country
WHERE CountryId = id;



/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetCountryByCityId;
CREATE procedure sp_GetCountryByCityId
(
	IN cityId INT
)


SELECT  co.CountryId as Id, co.Country as Name, co.CreatedBy, co.CreateDate, co.LastUpdateBy as LastUpdatedBy, co.LastUpdate
FROM country co
INNER JOIN city ci
ON co.CountryId = ci.CountryId
WHERE ci.Id = cityId;


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetCountries;
CREATE procedure sp_GetCountries
( 
)


SELECT  co.CountryId as Id, co.Country as Name, co.CreatedBy, co.CreateDate, co.LastUpdateBy as LastUpdatedBy, co.LastUpdate
FROM country co;



/*---------------------------------------City------------------------------------------------*/


/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_SaveCity$$
CREATE procedure sp_SaveCity
( 
	 INOUT id INT,
	 IN name varchar(50), 
     IN countryId INT,
	 IN createdBy varchar(50),
	 IN createDate datetime,
	 IN lastUpdatedBy varchar(50),
	 IN lastUpdate timestamp 
)

BEGIN
INSERT INTO city
	(CityId, City, CountryId, CreatedBy, CreateDate, LastUpdateBy, LastUpdate)
    VALUES (id, name, CountryId, createdBy, createDate, lastUpdatedBy, lastUpdate)
    on duplicate key update 
    City = name, 
    CountryId = countryId,
    LastUpdateBy = lastUpdatedBy,
    LastUpdate = lastUpdate;
    
    if (id = 0) THEN BEGIN SET id = (SELECT LAST_INSERT_ID()); END; END IF;
	SELECT id;
END$$
DELIMITER ;
	 
 
/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_DeleteCityById;
CREATE procedure sp_DeleteCityById
(
	IN id INT
)

DELETE FROM city WHERE CityId = id;


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetCityById;
CREATE PROCEDURE sp_GetCityById
(
	IN id INT
)

SELECT  CityId as Id, CountryId, City as Name, CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM city 
WHERE CityId = id;

/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetCities;
CREATE PROCEDURE sp_GetCities
( 
)

SELECT  CityId as Id, CountryId, City as Name, CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM city;



/*---------------------------------------Address------------------------------------------------*/
/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_SaveAddress$$
CREATE procedure sp_SaveAddress
( 
	 INOUT id INT,
	 IN address varchar(50), 
     IN address2  varchar(50),
     IN cityId INT,
     IN postalCode varchar(10),
     IN phone varchar(20),
	 IN createdBy varchar(50),
	 IN createDate datetime,
	 IN lastUpdatedBy varchar(50),
	 IN lastUpdate timestamp 
 
)
BEGIN

INSERT INTO address
	(AddressId, Address, Address2, CityId, PostalCode, Phone, CreatedBy, CreateDate, LastUpdateBy, LastUpdate)
    VALUES (id, address, address2, cityId, postalCode, phone, createdBy, createDate, lastUpdatedBy, lastUpdate)
    on duplicate key update 
    Address = address, 
    Address2 = address2,
    CityId = cityId,
    PostalCode = postalCode,
    Phone = phone,
    LastUpdateBy = lastUpdatedBy,
    LastUpdate = lastUpdate;
	 
    if (id = 0) THEN BEGIN SET id = (SELECT LAST_INSERT_ID()); END; END IF;
     SELECT id;
END$$
DELIMITER ;
 
/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_DeleteAddressById;
CREATE procedure sp_DeleteAddressById
(
	IN id INT
)

DELETE FROM address WHERE AddressId = id;


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetAddressById;
CREATE PROCEDURE sp_GetAddressById
(
	IN id INT
)

SELECT AddressId AS Id, Address, Address2, CityId, PostalCode, Phone, CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM address 
WHERE AddressId = id;



/*---------------------------------------Customer------------------------------------------------*/
/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_SaveCustomer$$
CREATE procedure sp_SaveCustomer
( 
	 INOUT id INT,
     IN name varchar(50), 
	 IN addressId INT,
	 IN active bit, 
	 IN createdBy varchar(50),
	 IN createDate datetime,
	 IN lastUpdatedBy varchar(50),
	 IN lastUpdate timestamp 
 
)
BEGIN

INSERT INTO customer
	(CustomerId, CustomerName, AddressId, Active, CreatedBy, CreateDate, LastUpdateBy, LastUpdate)
    VALUES (id, name, addressId, active, createdBy, createDate, lastUpdatedBy, lastUpdate)
    on duplicate key update 
    CustomerName = name, 
    AddressId = addressId,
    Active = active, 
    LastUpdateBy = lastUpdatedBy,
    LastUpdate = lastUpdate;
    
    if (id = 0) THEN BEGIN SET id = (SELECT LAST_INSERT_ID()); END; END IF;
   
    SELECT id;
END$$
DELIMITER ;
	 
 
/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_DeleteCustomerById$$
CREATE procedure sp_DeleteCustomerById
(
	IN id VARCHAR(64)
)

BEGIN
	DELETE FROM reminder WHERE AppointmentId IN(SELECT AppointmentId FROM customer WHERE CustomerId = id);
	DELETE FROM appointment WHERE CustomerId = id;
	DELETE FROM customer WHERE CustomerId = id;
END$$
DELIMITER ;

/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetCustomerById;
CREATE PROCEDURE sp_GetCustomerById
(
	IN id VARCHAR(64)
)
 
SELECT customerId as Id, CustomerName as Name, AddressId, Active, CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM  customer 
WHERE CustomerId = id;


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetCustomers;
CREATE PROCEDURE sp_GetCustomers
( 
)
 
SELECT CustomerId as Id, CustomerName as Name, AddressId, Active, CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM customer;


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_CustomerAppointmentCountWithOtherUsers;
CREATE PROCEDURE sp_CustomerAppointmentCountWithOtherUsers
( 
	IN userName VARCHAR(50),
    IN customerId int
)
 
SELECT COUNT(*)
FROM customer c 
INNER JOIN appointment a
ON c.CustomerId = a.CustomerId
WHERE c.customerid = customerId
AND a.CreatedBy != userName;




/*---------------------------------------Appointment------------------------------------------------*/
/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_SaveAppointment$$
CREATE procedure sp_SaveAppointment
( 
	 INOUT id INT,
     IN appointmentId INT,
	 IN customerId INT,
	 IN title varchar(255), 
	 IN description TEXT,
	 IN location TEXT,
	 IN contact TEXT,
	 IN uRL varchar(255),
	 IN start datetime,
	 IN end datetime,
	 IN createdBy varchar(50),
	 IN createDate datetime,
	 IN lastUpdatedBy varchar(50),
	 IN lastUpdate timestamp 
)

BEGIN

INSERT INTO appointment
	(AppointmentId, CustomerId, Title, Description, Location, Contact, URL, Start, End, CreatedBy, CreateDate, LastUpdateBy, LastUpdate)
    VALUES (id, customerId, title, description, location, contact, uRL, start, end, createdBy, createDate, lastUpdatedBy, lastUpdate)
    on duplicate key update 
    CustomerId = customerId,
    Title = title,
    Description = description,
    Location = location,
    Contact = contact,
    URL = uRL,
    Start = start,
    End = end,
    LastUpdateBy = lastUpdatedBy,
    LastUpdate = lastUpdate;
	 
    if (id = 0) THEN BEGIN SET id = (SELECT LAST_INSERT_ID()); END; END IF;
     SELECT id;
END$$
DELIMITER ;
 
/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_DeleteAppointmentById$$
CREATE procedure sp_DeleteAppointmentById
(
	IN id INT
)

BEGIN
	DELETE FROM reminder WHERE AppointmentId = id;
	DELETE FROM appointment WHERE AppointmentId = id;
END$$
DELIMITER ;


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetAppointmentById;
CREATE PROCEDURE sp_GetAppointmentById
(
	IN id INT
)
 

SELECT AppointmentId as Id, CustomerId, Title, Description, Location, Contact, URL, Start, End, 
CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM appointment 
WHERE AppointmentId = id;
 
 /*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetAppointmentByCustomerId;
CREATE PROCEDURE sp_GetAppointmentByCustomerId
(
	IN customerId INT
)
 

SELECT AppointmentId as Id, CustomerId, Title, Description, Location, Contact, URL, Start, End, 
CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM appointment 
WHERE CustomerId = customerId;


  /*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetMonthlyAppointments;
CREATE PROCEDURE sp_GetMonthlyAppointments
(
	IN startingDate datetime,
    IN endingDate datetime,
	IN userName varchar(50)
)
 

SELECT AppointmentId as Id, CustomerId, Title, Description, Location, Contact, URL, Start, End, 
CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM appointment 
WHERE Start BETWEEN startingDate AND endingDate
AND CreatedBy = userName;
  
 /*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetAppointments;
CREATE PROCEDURE sp_GetAppointments
( 
	IN userName VARCHAR(50)
)
 

SELECT AppointmentId as Id, CustomerId, Title, Description, Location, Contact, URL, Start, End, 
CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM appointment
WHERE CreatedBy = userName;
 
 
/*---------------------------------------Reminder------------------------------------------------*/
/*-------------------------------------CREATE PROC------------------------------------------*/
DELIMITER $$
DROP procedure IF EXISTS sp_SaveReminder$$
CREATE procedure sp_SaveReminder
( 
	 INOUT id INT,
	 IN reminderDate datetime,
	 IN snoozeIncrement int,
	 IN incrementTypeId INT,
	 IN appointmentId INT,
	 IN createdBy varchar(50),
	 IN createDate datetime,
	 IN lastUpdatedBy varchar(50),
	 IN lastUpdate timestamp 
 
)
BEGIN
INSERT INTO reminder
	(ReminderId, ReminderDate, SnoozeIncrement, IncrementTypeId, AppointmentId, CreatedBy, CreateDate, LastUpdateBy, LastUpdate)
    VALUES (id, reminderDate, snoozeIncrement, incrementTypeId, appointmentId, createdBy, createDate, lastUpdatedBy, lastUpdate)
    on duplicate key update 
    ReminderDate = reminderDate,
    SnoozeIncrement = snoozeIncrement,
    IncrementTypeid = incrementTypeId,
    AppointmentId = appointmentId,
    LastUpdateBy = lastUpdatedBy,
    LastUpdate = lastUpdate;
    
    if (id = 0) THEN BEGIN SET id = (SELECT LAST_INSERT_ID()); END; END IF;
     SELECT id;
END$$
DELIMITER ;
	 
 
/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_DeleteReminderById;
CREATE procedure sp_DeleteReminderById
(
	IN id VARCHAR(64)
)

DELETE FROM reminder WHERE ReminderId = id;


/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetReminderById;
CREATE PROCEDURE sp_GetReminderById
(
	IN id INT
)
 
SELECT ReminderId as Id, ReminderDate, SnoozeIncrement, IncrementTypeId, AppointmentId, 
CreatedBy, CreateDate, LastUpdateBy as LastUpdatedBy, LastUpdate
FROM reminder 
WHERE ReminderId = id;

    
    
/*-------------------------------------CREATE PROC------------------------------------------*/
DROP procedure IF EXISTS sp_GetRemindersByDateAndUser;
CREATE PROCEDURE sp_GetRemindersByDateAndUser
(
	IN name VARCHAR(50),
    IN start datetime,
    IN end datetime
)
 
SELECT ReminderId as Id, ReminderDate, SnoozeIncrement, IncrementTypeId, r.AppointmentId, 
r.CreatedBy, r.CreateDate, r.LastUpdateBy as LastUpdatedBy, r.LastUpdate
FROM reminder r
INNER JOIN appointment a
ON r.AppointmentId = a.AppointmentId
WHERE a.CreatedBy = name
AND a.Start BETWEEN start and end;