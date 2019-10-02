-- Grants annotator users with the permissions needed to use the MiMus application.
-- Ideally, only the minimum set of permissions should be provided.
GRANT UPDATE ON mimus.document TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.has_materia TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.entity TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.bibliografia TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.artista TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.casa TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.genere_literari TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.instrument TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.lloc TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.ofici TO mimus01@localhost;
GRANT SELECT, INSERT, UPDATE, DELETE ON mimus.promotor TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.entity_instance TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.transcription TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.referencia TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.note TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.relation TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.te_ofici TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.te_casa TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.serveix_a TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.resideix_a TO mimus01@localhost;
GRANT SELECT, INSERT, DELETE ON mimus.moviment TO mimus01@localhost;