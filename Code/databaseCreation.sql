CREATE DATABASE deduplicator;
USE deduplicator;

CREATE TABLE deduplicator.action_type
(
    type VARCHAR(50) NOT NULL PRIMARY KEY
    
);

CREATE TABLE deduplicator.authentication_details(
    username varchar(50) not null primary key,
    password varchar(640) 
);

CREATE TABLE deduplicator.scheduler(
    id int not null primary key,
    monthly int default null,
    weekly tinyint default null,
    hour int,
    repeated boolean,
    date_start date

);


CREATE TABLE deduplicator.action
(
    id int not null primary key,
    filepath VARCHAR(255),
    new_filepath VARCHAR(255) default null,
    action_type VARCHAR(50) not null,
    executed boolean,
    date_added timestamp,
    user varchar(50),
    scheduler int null,
    FOREIGN KEY(action_type) REFERENCES action_type(type),
    FOREIGN key(user) REFERENCES authentication_details(username),
    FOREIGN key(scheduler) REFERENCES scheduler(id)
);

CREATE TABLE deduplicator.report(
    id int not null primary key,
    duration int,
    start timestamp,
    duplicate_count int,
    user varchar(50),
    FOREIGN KEY(user) REFERENCES authentication_details(username)
);


CREATE TABLE deduplicator.file(
    path varchar(255) not null primary key,
    last_modified timestamp not null,
    hash varchar(16),
    size int,
    report_id int,
    FOREIGN key(report_id) REFERENCES report(id)
);

CREATE TABLE deduplicator.global_path(
    path varchar(255) not null primary key,
    file BOOLEAN,
    ignore_file BOOLEAN,
    date timestamp
);
