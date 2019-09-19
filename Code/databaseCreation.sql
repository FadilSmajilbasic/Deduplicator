CREATE DATABASE Deduplicator;
USE Deduplicator;

CREATE TABLE Deduplicator.tipoAzione
(
    tipo VARCHAR(50) NOT NULL PRIMARY KEY
    
);

CREATE TABLE Deduplicator.azioni
(
    percorsoFile VARCHAR(255) not null primary key,
    tipoAzione VARCHAR(50),
    FOREIGN KEY(tipoAzione) REFERENCES tipoAzione(tipo)
    
);



CREATE TABLE Deduplicator.tipoPercorso
(
    tipo VARCHAR(50) NOT NULL PRIMARY KEY
    
);


CREATE TABLE Deduplicator.percorsi
(
    percorso varchar(255) not null PRIMARY key, -- primary key column
    file boolean NOT null,
    date TIMESTAMP not NULL,
    tipoPercorso VARCHAR(50),
    FOREIGN KEY(tipoPercorso) REFERENCES tipoPercorso(tipo)
    
);


CREATE TABLE Deduplicator.rapporti
(
    timestamp TIMESTAMP NOT NULL PRIMARY KEY, -- primary key column
    n_duplicati SMALLINT NOT NULL,
    origin VARCHAR(50)
);


CREATE TABLE Deduplicator.file
(
    percorso varchar(255) NOT NULL PRIMARY KEY, -- primary key column
    dataModifica date not null,
    hash BINARY(16) not null,
    grandezza BIGINT not null,
    rapporto TIMESTAMP,
    FOREIGN KEY(rapporto) REFERENCES rapporti(timestamp)
   
);
