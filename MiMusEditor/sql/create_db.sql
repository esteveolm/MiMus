CREATE SCHEMA Mimus;
USE Mimus;

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
	Llengua varchar(20) NOT NULL,
	Materies text NOT NULL,
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
	doc_id bigint(20) NOT NULL,
	biblio_id bigint(20) NOT NULL,
	FOREIGN KEY (doc_id)
		REFERENCES Document(id),
	FOREIGN KEY (biblio_id)
		REFERENCES Bibliografia(id),
	PRIMARY KEY (id)
);

CREATE TABLE Entity (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (id)
);

CREATE TABLE Artista (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	tractament varchar(100) NULL,
	nom varchar(100) NULL,
	cognom varchar(100) NULL,
	sobrenom varchar(100) NULL,
	distintiu varchar(100) NULL,
	genere int(1) NULL,
	religion int(1) NULL,
	origen int(1) NULL,
	FOREIGN KEY (ent_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Promotor (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	nom varchar(100) NULL,
	cognom varchar(100) NULL,
	sobrenom varchar(100) NULL,
	distintiu varchar(100) NULL,
	genere int(1) NULL,
	FOREIGN KEY (ent_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Instrument (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_id bigint(20) NOT NULL,
	nom varchar(100) NULL,
	familia int(1) NULL,
	classe int(1) NULL,
	part varchar(100) NULL,
	FOREIGN KEY (ent_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Ofici (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	terme varchar(100) NULL,
	especialitat varchar(100) NULL,
	instrument_id bigint(20) NULL,
	FOREIGN KEY (ent_id)
		REFERENCES Entity(id),
	FOREIGN KEY (instrument_id)
		REFERENCES Instrument(id),
	PRIMARY KEY (id)
);

CREATE TABLE Casa (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	titol varchar(100) NULL,
	cort varchar(100) NULL,
	FOREIGN KEY (ent_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE Lloc (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	regne int(1) NULL,
	area int(1) NULL,
	FOREIGN KEY (ent_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE EntityInstance (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_id bigint(20) NOT NULL,
	doc_id bigint(20) NOT NULL,
	FOREIGN KEY (ent_id)
		REFERENCES Entity(id),
	FOREIGN KEY (doc_id)
		REFERENCES Document(id),
	PRIMARY KEY (id)
);

CREATE TABLE Transcription (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	ent_inst_id bigint(20) NOT NULL,
	doc_id bigint(20) NOT NULL,
	FOREIGN KEY (ent_inst_id)
		REFERENCES EntityInstance(id),
	FOREIGN KEY (doc_id)
		REFERENCES Document(id),
	PRIMARY KEY (id)
);

CREATE TABLE Relation (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	doc_id bigint(20) NOT NULL,
	FOREIGN KEY (doc_id)
		REFERENCES Entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE TeOfici (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	rel_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	ofici_id bigint(20) NOT NULL,
	FOREIGN KEY (rel_id)
		REFERENCES Relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES Artista(id),
	FOREIGN KEY (ofici_id)
		REFERENCES Ofici(id),
	PRIMARY KEY (id)
);

CREATE TABLE TeCasa (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	rel_id bigint(20) NOT NULL,
	promotor_id bigint(20) NOT NULL,
	casa_id bigint(20) NOT NULL,
	FOREIGN KEY (rel_id)
		REFERENCES Relation(id),
	FOREIGN KEY (promotor_id)
		REFERENCES Promotor(id),
	FOREIGN KEY (casa_id)
		REFERENCES Casa(id),
	PRIMARY KEY (id)
);

CREATE TABLE ServeixA (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	rel_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	promotor_id bigint(20) NOT NULL,
	FOREIGN KEY (rel_id)
		REFERENCES Relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES Artista(id),
	FOREIGN KEY (promotor_id)
		REFERENCES Promotor(id),
	PRIMARY KEY (id)
);

CREATE TABLE ResideixA (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	rel_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	lloc_id bigint(20) NOT NULL,
	FOREIGN KEY (rel_id)
		REFERENCES Relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES Artista(id),
	FOREIGN KEY (lloc_id)
		REFERENCES Lloc(id),
	PRIMARY KEY (id)
);
