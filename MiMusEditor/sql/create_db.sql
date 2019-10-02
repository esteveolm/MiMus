-- Schema follows character set utf8mb4.
CREATE SCHEMA mimus;
USE mimus;
ALTER SCHEMA mimus  DEFAULT CHARACTER SET utf8mb4  DEFAULT COLLATE utf8mb4_general_ci;

CREATE TABLE llengua (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    llengua_name varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO llengua (llengua_name) VALUES ("-");
INSERT INTO llengua (llengua_name) VALUES ("llatí");
INSERT INTO llengua (llengua_name) VALUES ("català");
INSERT INTO llengua (llengua_name) VALUES ("castellà");
INSERT INTO llengua (llengua_name) VALUES ("aragonès/castellà");

-- Document has dates specified in several fields.
-- h_any1 and so on are boolean variables for hypothetical date fields.
-- d_any1 and so on are boolean variables for unknown date fields.
CREATE TABLE document (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	numeracio varchar(20) NOT NULL,
	any1 int(4) NOT NULL,
	any2 int(4) NULL,
	mes1 int(2) NULL,
	mes2 int(2) NULL,
	dia1 int(2) NULL,
	dia2 bit NULL,
	h_any1 bit NULL,
	h_any2 bit NULL,
	h_mes1 bit NULL,
	h_mes2 bit NULL,
	h_dia1 bit NULL,
	h_dia2 bit NULL,
	d_any1 bit NULL,
	d_any2 bit NULL,
	d_mes1 bit NULL,
	d_mes2 bit NULL,
	d_dia1 bit NULL,
	d_dia2 bit NULL,
	lloc1 varchar(100) NULL,
	lloc2 varchar(100) NULL,
	regest varchar(1024) NULL,
	lib1_arxiu varchar(100) NULL,
	lib1_serie varchar(100) NULL,
	lib1_subserie varchar(100) NULL,
	lib1_subserie2 varchar(100) NULL,
	lib1_numero varchar(100) NULL,
	lib1_pagina varchar(100) NULL,
	lib2_arxiu varchar(100) NULL,
	lib2_serie varchar(100) NULL,
	lib2_subserie varchar(100) NULL,
	lib2_subserie2 varchar(100) NULL,
	lib2_numero varchar(100) NULL,
	lib2_pagina varchar(100) NULL,
	edicions varchar(100) NULL,
	registres varchar(100) NULL,
	citacions varchar(100) NULL,
	transcripcio text NULL,
	llengua_id bigint(20) NULL,
    state_annot int(1) NULL,
    state_rev int(1) NULL,
    FOREIGN KEY (llengua_id)
		REFERENCES llengua(id),
	PRIMARY KEY (id)
);

CREATE TABLE materia (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    materia_name varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO materia (materia_name) VALUES ("actuació / performance");
INSERT INTO materia (materia_name) VALUES ("ambaixada / missatgeria");
INSERT INTO materia (materia_name) VALUES ("artista visitant");
INSERT INTO materia (materia_name) VALUES ("Capella reial");
INSERT INTO materia (materia_name) VALUES ("celebracions");
INSERT INTO materia (materia_name) VALUES ("companyies");
INSERT INTO materia (materia_name) VALUES ("compravenda");
INSERT INTO materia (materia_name) VALUES ("concessió de rendes");
INSERT INTO materia (materia_name) VALUES ("donació (immobles)");
INSERT INTO materia (materia_name) VALUES ("donació graciosa");
INSERT INTO materia (materia_name) VALUES ("enfranquiment");
INSERT INTO materia (materia_name) VALUES ("escoles");
INSERT INTO materia (materia_name) VALUES ("família");
INSERT INTO materia (materia_name) VALUES ("guiatge / salconduit");
INSERT INTO materia (materia_name) VALUES ("itinerància");
INSERT INTO materia (materia_name) VALUES ("justícia");
INSERT INTO materia (materia_name) VALUES ("legislació");
INSERT INTO materia (materia_name) VALUES ("llibres i manuscrits");
INSERT INTO materia (materia_name) VALUES ("moviment d’artistes");
INSERT INTO materia (materia_name) VALUES ("pagament");
INSERT INTO materia (materia_name) VALUES ("pagament de cavalcadura");
INSERT INTO materia (materia_name) VALUES ("pagament de despeses");
INSERT INTO materia (materia_name) VALUES ("pagament de salari");
INSERT INTO materia (materia_name) VALUES ("pagament de vestit");
INSERT INTO materia (materia_name) VALUES ("patrimoni");
INSERT INTO materia (materia_name) VALUES ("petició d’artistes");
INSERT INTO materia (materia_name) VALUES ("protecció");
INSERT INTO materia (materia_name) VALUES ("recomanació d’artistes");

-- Document-Materia is a many-to-many relationship reflected on table has_materia.
CREATE TABLE has_materia (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    document_id bigint(20) NOT NULL,
    materia_id bigint(20) NOT NULL,
    FOREIGN KEY (document_id)
		REFERENCES document(id),
	FOREIGN KEY (materia_id)
		REFERENCES materia(id),
	PRIMARY KEY (id)
);

CREATE TABLE bibliografia (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	autor1 varchar(100) NULL,
	autor2 varchar(100) NULL,
	autor3 varchar(100) NULL,
	autor4 varchar(100) NULL,
	autor_secondari1 varchar(100) NULL,
	autor_secondari2 varchar(100) NULL,
	autor_secondari3 varchar(100) NULL,
	autor_secondari4 varchar(100) NULL,
	autor_secondari5 varchar(100) NULL,
	autor_secondari6 varchar(100) NULL,
	any_ varchar(100) NULL,
	distincio varchar(1) NULL,
	titol varchar(100) NULL,
	titol_principal varchar(100) NULL,
	volum varchar(100) NULL,
	lloc varchar(100) NULL,
	editorial varchar(100) NULL,
	serie varchar(100) NULL,
	pagines varchar(100) NULL,
	referencia_curta varchar(1024) NULL,
	PRIMARY KEY (id)
);

-- Triangular relation Document - Reference - Note
-- Reference-Note we create it without foreign key constraint
-- And deal with it in the code
CREATE TABLE referencia (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    ref_type varchar(20) NOT NULL,
    pages varchar(100) NULL,
	document_id bigint(20) NOT NULL,
	bibliografia_id bigint(20) NOT NULL,
    note_id bigint(20) NULL,
	FOREIGN KEY (document_id)
		REFERENCES document(id),
	FOREIGN KEY (bibliografia_id)
		REFERENCES bibliografia(id),
	UNIQUE (document_id, bibliografia_id),
	PRIMARY KEY (id)
);

CREATE TABLE entity_types (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    entity_name varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO entity_types (entity_name) VALUES ("artista");
INSERT INTO entity_types (entity_name) VALUES ("promotor");
INSERT INTO entity_types (entity_name) VALUES ("ofici");
INSERT INTO entity_types (entity_name) VALUES ("casa");
INSERT INTO entity_types (entity_name) VALUES ("instrument");
INSERT INTO entity_types (entity_name) VALUES ("lloc");
INSERT INTO entity_types (entity_name) VALUES ("genere_literari");

-- entity.entity_id is a foreign key to the ID of the child table.
-- Because the table in question depends on the entity type, we can't
-- specify it on the schema, and this is handled by the MiMus code directly.
CREATE TABLE entity (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    entity_type_id bigint(20) NOT NULL,
    entity_id bigint(20) NOT NULL,
    FOREIGN KEY (entity_type_id)
		REFERENCES entity_types(id),
	PRIMARY KEY (id)
);

CREATE TABLE artista (
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
    observacions varchar(1024) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE promotor (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	nom varchar(100) NULL,
	cognom varchar(100) NULL,
	sobrenom varchar(100) NULL,
	distintiu varchar(100) NULL,
	genere int(1) NULL,
    observacions varchar(1024) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE instrument (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom varchar(100) NULL,
	familia int(1) NULL,
	classe int(1) NULL,
	part varchar(100) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE ofici (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	terme varchar(100) NULL,
	especialitat varchar(100) NULL,
	instrument_id bigint(20) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	FOREIGN KEY (instrument_id)
		REFERENCES instrument(id),
	PRIMARY KEY (id)
);

CREATE TABLE casa (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	titol varchar(100) NULL,
	cort varchar(100) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE lloc (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	nom_complet varchar(100) NULL,
	regne int(1) NULL,
	area int(1) NULL,
	FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE genere_literari (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    entity_id bigint(20) NOT NULL,
    nom_complet varchar(100) NULL,
    nom_frances varchar(100) NULL,
    nom_occita varchar(100) NULL,
    definicio varchar(100) NULL,
    FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	PRIMARY KEY (id)
);

CREATE TABLE entity_instance (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_id bigint(20) NOT NULL,
	document_id bigint(20) NOT NULL,
	FOREIGN KEY (entity_id)
		REFERENCES entity(id),
	FOREIGN KEY (document_id)
		REFERENCES document(id),
	UNIQUE (entity_id, document_id),
	PRIMARY KEY (id)
);

CREATE TABLE transcription (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	entity_instance_id bigint(20) NOT NULL,
    selected_text varchar(200) NOT NULL,
    form varchar(200) NOT NULL,
    coords_from int(4) NOT NULL,
    coords_to int(4) NOT NULL,
	FOREIGN KEY (entity_instance_id)
		REFERENCES entity_instance(id),
	PRIMARY KEY (id)
);

CREATE TABLE relation_types (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    relation_name varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO relation_types (relation_name) VALUES ("te_ofici");
INSERT INTO relation_types (relation_name) VALUES ("te_casa");
INSERT INTO relation_types (relation_name) VALUES ("serveix_a");
INSERT INTO relation_types (relation_name) VALUES ("resideix_a");
INSERT INTO relation_types (relation_name) VALUES ("moviment");

-- relation.relation_id is a foreign key to the ID of the child table.
-- Because the table in question depends on the relation type, we can't
-- specify it on the schema, and this is handled by the MiMus code directly.
CREATE TABLE relation (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    relation_type_id bigint(20) NOT NULL,
    relation_id bigint(20) NOT NULL,
	document_id bigint(20) NOT NULL,
    FOREIGN KEY (relation_type_id)
		REFERENCES relation_types(id),
	FOREIGN KEY (document_id)
		REFERENCES document(id),
	PRIMARY KEY (id)
);

CREATE TABLE te_ofici (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	ofici_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES artista(id),
	FOREIGN KEY (ofici_id)
		REFERENCES ofici(id),
	UNIQUE (artista_id, ofici_id, relation_id),
	PRIMARY KEY (id)
);

CREATE TABLE te_casa (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	promotor_id bigint(20) NOT NULL,
	casa_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES relation(id),
	FOREIGN KEY (promotor_id)
		REFERENCES promotor(id),
	FOREIGN KEY (casa_id)
		REFERENCES casa(id),
	UNIQUE (promotor_id, casa_id, relation_id),
	PRIMARY KEY (id)
);

CREATE TABLE serveix_a (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	promotor_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES artista(id),
	FOREIGN KEY (promotor_id)
		REFERENCES promotor(id),
	UNIQUE (artista_id, promotor_id, relation_id),
	PRIMARY KEY (id)
);

CREATE TABLE resideix_a (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
	artista_id bigint(20) NOT NULL,
	lloc_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES artista(id),
	FOREIGN KEY (lloc_id)
		REFERENCES lloc(id),
	UNIQUE (artista_id, lloc_id, relation_id),
	PRIMARY KEY (id)
);

CREATE TABLE moviment (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	relation_id bigint(20) NOT NULL,
    artista_id bigint(20) NOT NULL,
    origen_type_id bigint(20) NOT NULL,
    origen_id bigint(20) NOT NULL,
    destino_type_id bigint(20) NOT NULL,
    destino_id bigint(20) NOT NULL,
	FOREIGN KEY (relation_id)
		REFERENCES relation(id),
	FOREIGN KEY (artista_id)
		REFERENCES artista(id),
	FOREIGN KEY (origen_type_id)
		REFERENCES entity_types(id),
	FOREIGN KEY (destino_type_id)
		REFERENCES entity_types(id),
	FOREIGN KEY (origen_id)
		REFERENCES entity(id),
	FOREIGN KEY (destino_id)
		REFERENCES entity(id),
	UNIQUE (artista_id, 
		origen_type_id, 
        origen_id, 
		destino_type_id, 
        destino_id,
        relation_id),
	PRIMARY KEY (id)
);

CREATE TABLE note_types (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    note_name varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO note_types(note_name) VALUES ("nota");
INSERT INTO note_types(note_name) VALUES ("referencia");
INSERT INTO note_types(note_name) VALUES ("nota_bibliografica");
INSERT INTO note_types(note_name) VALUES ("nota_data");
INSERT INTO note_types(note_name) VALUES ("nota_arxiu");

CREATE TABLE note (
	id bigint(20) NOT NULL AUTO_INCREMENT,
    document_id bigint(20) NOT NULL,
    note_type_id bigint(20) NOT NULL,
    note_text varchar(1024) NOT NULL,
    FOREIGN KEY (document_id)
		REFERENCES document(id),
	FOREIGN KEY (note_type_id)
		REFERENCES note_types(id),
	PRIMARY KEY (id)
);

INSERT INTO bibliografia (referencia_curta) VALUES ("Desconegut");