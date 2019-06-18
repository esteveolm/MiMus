CREATE SCHEMA Mimus;
USE Mimus;

CREATE TABLE Llengua (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    LlenguaName varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO Llengua (LlenguaName) VALUES ("llatí");
INSERT INTO Llengua (LlenguaName) VALUES ("català");
INSERT INTO Llengua (LlenguaName) VALUES ("castellà");
INSERT INTO Llengua (LlenguaName) VALUES ("aragonès/castellà");

CREATE TABLE Document (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	Numeracio varchar(20) NOT NULL,
	Any int(4) NOT NULL,
	Any2 int(4) NULL,
	Mes int(2) NULL,
	Mes2 int(2) NULL,
	Dia int(2) NULL,
	Dia2 bit NULL,
	hAny bit NULL,
	hAny2 bit NULL,
	hMes bit NULL,
	hMes2 bit NULL,
	hDia bit NULL,
	hDia2 bit NULL,
	dAny bit NULL,
	dAny2 bit NULL,
	dMes bit NULL,
	dMes2 bit NULL,
	dDia bit NULL,
	dDia2 bit NULL,
	Lloc varchar(100) NULL,
	Lloc2 varchar(100) NULL,
	Regest varchar(1024) NULL,
	lib1Arxiu varchar(100) NULL,
	lib1Serie varchar(100) NULL,
	lib1Subserie varchar(100) NULL,
	lib1Subserie2 varchar(100) NULL,
	lib1Numero varchar(100) NULL,
	lib1Pagina varchar(100) NULL,
	lib2Arxiu varchar(100) NULL,
	lib2Serie varchar(100) NULL,
	lib2Subserie varchar(100) NULL,
	lib2Subserie2 varchar(100) NULL,
	lib2Numero varchar(100) NULL,
	lib2Pagina varchar(100) NULL,
	Edicions varchar(100) NULL,
	Registres varchar(100) NULL,
	Citacions varchar(100) NULL,
	Transcripcio text NULL,
	Notes varchar(1024) NULL,
	llengua_id bigint(20) NOT NULL,
    FOREIGN KEY (llengua_id)
		REFERENCES Llengua(id),
	PRIMARY KEY (id)
);

CREATE TABLE Materia (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    MateriaName varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO Materia (MateriaName) VALUES ("actuació / performance");
INSERT INTO Materia (MateriaName) VALUES ("ambaixada / missatgeria");
INSERT INTO Materia (MateriaName) VALUES ("artista visitant");
INSERT INTO Materia (MateriaName) VALUES ("capella reial");
INSERT INTO Materia (MateriaName) VALUES ("celebracions");
INSERT INTO Materia (MateriaName) VALUES ("companyies");
INSERT INTO Materia (MateriaName) VALUES ("compravenda");
INSERT INTO Materia (MateriaName) VALUES ("compra d’instruments");
INSERT INTO Materia (MateriaName) VALUES ("concessió de rendes");
INSERT INTO Materia (MateriaName) VALUES ("donació (immobles)");
INSERT INTO Materia (MateriaName) VALUES ("donació graciosa");
INSERT INTO Materia (MateriaName) VALUES ("enfranquiment");
INSERT INTO Materia (MateriaName) VALUES ("escoles");
INSERT INTO Materia (MateriaName) VALUES ("família");
INSERT INTO Materia (MateriaName) VALUES ("guiatge / salconduit (per a la tipologia documental)");
INSERT INTO Materia (MateriaName) VALUES ("itinerància");
INSERT INTO Materia (MateriaName) VALUES ("justícia");
INSERT INTO Materia (MateriaName) VALUES ("legislació");
INSERT INTO Materia (MateriaName) VALUES ("llibres i manuscrits");
INSERT INTO Materia (MateriaName) VALUES ("manuscrit");
INSERT INTO Materia (MateriaName) VALUES ("moviment d’artistes");
INSERT INTO Materia (MateriaName) VALUES ("pagament");
INSERT INTO Materia (MateriaName) VALUES ("pagament (de cavalcadura)");
INSERT INTO Materia (MateriaName) VALUES ("pagament (de salari)");
INSERT INTO Materia (MateriaName) VALUES ("pagament (de vestit)");
INSERT INTO Materia (MateriaName) VALUES ("patrimoni");
INSERT INTO Materia (MateriaName) VALUES ("petició d’artistes");
INSERT INTO Materia (MateriaName) VALUES ("protecció");
INSERT INTO Materia (MateriaName) VALUES ("recomanació d’artistes");

CREATE TABLE HasMateria (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    document_id bigint(20) NOT NULL,
    materia_id bigint(20) NOT NULL,
    FOREIGN KEY (document_id)
		REFERENCES Document(id),
	FOREIGN KEY (materia_id)
		REFERENCES Materia(id),
	PRIMARY KEY (id)
);

CREATE TABLE Bibliografia (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	Autor1 varchar(100) NULL,
	Autor2 varchar(100) NULL,
	Autor3 varchar(100) NULL,
	Autor4 varchar(100) NULL,
	AutorSecondari1 varchar(100) NULL,
	AutorSecondari2 varchar(100) NULL,
	AutorSecondari3 varchar(100) NULL,
	AutorSecondari4 varchar(100) NULL,
	AutorSecondari5 varchar(100) NULL,
	AutorSecondari6 varchar(100) NULL,
	Any int(4) NULL,
	Distincio varchar(1) NULL,
	Titol varchar(100) NULL,
	TitolPrincipal varchar(100) NULL,
	Volum varchar(100) NULL,
	Lloc varchar(100) NULL,
	Editorial varchar(100) NULL,
	Serie varchar(100) NULL,
	Pagines varchar(100) NULL,
	ReferenciaCurta varchar(1024) NULL,
	PRIMARY KEY (id)
);

CREATE TABLE Referencia (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	document_id bigint(20) NOT NULL,
	bibliografia_id bigint(20) NOT NULL,
	FOREIGN KEY (document_id)
		REFERENCES Document(id),
	FOREIGN KEY (bibliografia_id)
		REFERENCES Bibliografia(id),
	PRIMARY KEY (id)
);

CREATE TABLE EntityTypes (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    EntityName varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO EntityTypes (EntityName) VALUES ("Artista");
INSERT INTO EntityTypes (EntityName) VALUES ("Promotor");
INSERT INTO EntityTypes (EntityName) VALUES ("Ofici");
INSERT INTO EntityTypes (EntityName) VALUES ("Casa");
INSERT INTO EntityTypes (EntityName) VALUES ("Instrument");
INSERT INTO EntityTypes (EntityName) VALUES ("Lloc");

CREATE TABLE Entity (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    entity_type_id bigint(20) NOT NULL,
    entity_id bigint(20) NOT NULL,
    FOREIGN KEY (entity_type_id)
		REFERENCES EntityTypes(id),
	PRIMARY KEY (id)
);

CREATE TABLE Artista (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	tractament varchar(100) NULL,
	nom varchar(100) NULL,
	cognom varchar(100) NULL,
	sobrenom varchar(100) NULL,
	distintiu varchar(100) NULL,
	genere int(1) NULL,
	religio int(1) NULL,
	origen varchar(100) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Promotor (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	nom varchar(100) NULL,
	cognom varchar(100) NULL,
	sobrenom varchar(100) NULL,
	distintiu varchar(100) NULL,
	genere int(1) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Instrument (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom varchar(100) NULL,
	familia int(1) NULL,
	classe int(1) NULL,
	part varchar(100) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Ofici (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	terme varchar(100) NULL,
	especialitat varchar(100) NULL,
	instrument_id bigint(20) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES Entity(id),
	FOREIGN KEY (instrument_id)
		REFERENCES Instrument(id),
	PRIMARY KEY (id)
);

CREATE TABLE Casa (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	titol varchar(100) NULL,
	cort varchar(100) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Lloc (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	regne int(1) NULL,
	area int(1) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE EntityInstance (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	document_id bigint(20) NOT NULL,
	FOREIGN KEY (entity_id)
		REFERENCES Entity(id),
	FOREIGN KEY (document_id)
		REFERENCES Document(id),
	PRIMARY KEY (id)
);

CREATE TABLE Transcription (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_instance_id bigint(20) NOT NULL,
	FOREIGN KEY (entity_instance_id)
		REFERENCES EntityInstance(id),
	PRIMARY KEY (id)
);

CREATE TABLE RelationTypes (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    RelationName varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO RelationTypes (RelationName) VALUES ("TeOfici");
INSERT INTO RelationTypes (RelationName) VALUES ("TeCasa");
INSERT INTO RelationTypes (RelationName) VALUES ("ServeixA");
INSERT INTO RelationTypes (RelationName) VALUES ("ResideixA");
INSERT INTO RelationTypes (RelationName) VALUES ("Moviment");

CREATE TABLE Relation (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    relation_type_id bigint(20) NOT NULL,
    relation_id bigint(20) NOT NULL,
	document_id bigint(20) NOT NULL,
    FOREIGN KEY (relation_type_id)
		REFERENCES RelationTypes(id),
	FOREIGN KEY (document_id)
		REFERENCES Document(id),
	PRIMARY KEY (id)
);

CREATE TABLE TeOfici (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	ofici_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES Relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES Artista(id),
	FOREIGN KEY (ofici_id)
		REFERENCES Ofici(id),
	PRIMARY KEY (id)
);

CREATE TABLE TeCasa (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	promotor_id bigint(20) NOT NULL,
	casa_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES Relation(id),
	FOREIGN KEY (promotor_id)
		REFERENCES Promotor(id),
	FOREIGN KEY (casa_id)
		REFERENCES Casa(id),
	PRIMARY KEY (id)
);

CREATE TABLE ServeixA (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	promotor_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES Relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES Artista(id),
	FOREIGN KEY (promotor_id)
		REFERENCES Promotor(id),
	PRIMARY KEY (id)
);

CREATE TABLE ResideixA (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	lloc_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES Relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES Artista(id),
	FOREIGN KEY (lloc_id)
		REFERENCES Lloc(id),
	PRIMARY KEY (id)
);

CREATE TABLE Moviment (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
    artista_id bigint(20) NOT NULL,
    origen_type_id bigint(20) NOT NULL,
    origen_id bigint(20) NOT NULL,
    destino_type_id bigint(20) NOT NULL,
    destino_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES Relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES Artista(id),
	FOREIGN KEY (origen_type_id)
		REFERENCES EntityTypes(id),
	FOREIGN KEY (destino_type_id)
		REFERENCES EntityTypes(id),
	FOREIGN KEY (origen_id)
		REFERENCES Entity(id),
	FOREIGN KEY (destino_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

INSERT INTO Bibliografia (ReferenciaCurta) VALUES ("Desconegut");