DROP Table Members cascade;
drop table Trainers cascade;
drop table FitGoals cascade;
drop table HealthMetr cascade;
drop table Classes cascade;
DROP Table Sessions cascade;
drop table Rooms cascade;


CREATE TABLE Members (
	member_id	SERIAL,	--PRIMARY KEY, --optional? email makes more sense to be the primary key?
	first_name	VARCHAR(255) NOT NULL,
	last_name	VARCHAR(255) NOT NULL,
	email		VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,
	phone		VARCHAR(15),
	birth_date	date DEFAULT CURRENT_DATE,
	gender		VARCHAR(255)
);

CREATE TABLE Trainers (
	trainer_id	serial primary key,
	start_times Timestamp[],
	end_times Timestamp[],
	first_name	VARCHAR(255) NOT NULL,
	last_name	VARCHAR(255) NOT NULL,
	email		VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE FitGoals (
	goal_id	SERIAL	PRIMARY KEY,
	goal_name	VARCHAR(255) NOT NULL,
	goal_number INT,
	member_email VARCHAR(255) NOT NULL,
	creation_time	Timestamp DEFAULT CURRENT_timestamp,
	FOREIGN KEY (member_email)
		REFERENCES Members (email)
);

CREATE TABLE HealthMetr (
	metric_id	SERIAL	PRIMARY KEY,
	member_email	VARCHAR(255) NOT NULL,
	height INT,
	weight_ INT,
	heart_rate INT,
	metric_timestamp	Timestamp DEFAULT CURRENT_timestamp,
	FOREIGN KEY (member_email)
		REFERENCES Members (email)
);

CREATE TABLE Classes (
	class_id	SERIAL	PRIMARY KEY,
	start_time	Timestamp,
	end_time	Timestamp,
	capacity	INT NOT NULL,
	room_number	INT,
	member_email VARCHAR(255)[],
	trainer_id  INT,
	FOREIGN KEY (trainer_id)
		REFERENCES Trainers (trainer_id)
);

CREATE TABLE Sessions (
	session_id	SERIAL	PRIMARY KEY,
	start_time	Timestamp,
	end_time	Timestamp,
	room_number	INT,
	member_email VARCHAR(255),
	trainer_id  INT,
	FOREIGN KEY (member_email)
		REFERENCES Members (email),
	FOREIGN KEY (trainer_id)
		REFERENCES Trainers (trainer_id)
);

CREATE TABLE Rooms (
	room_number 	Serial Primary Key,
	class_id 	INT[], 
	session_id 	INT[]
);