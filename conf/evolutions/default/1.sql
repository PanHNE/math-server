# SYNERGIA SCHEMA

# --- !Ups
CREATE TABLE Users (
	id                          	UUID UNIQUE             	NOT NULL,
	provider_id                	    CHARACTER VARYING       	NOT NULL,
	role_type                  	    CHARACTER VARYING       	NOT NULL,
	provider_key				    CHARACTER VARYING 			NOT NULL,
	password						CHARACTER VARYING 			NOT NULL,
	first_name 						CHARACTER VARYING 				NULL,
	last_name						CHARACTER VARYING 				NULL,
	phone_number					CHARACTER VARYING 				NULL,
	reset_password_token		    CHARACTER VARYING       	 	NULL,

	PRIMARY KEY (id)
);

CREATE TABLE Files (
    id                              UUID UNIQUE             	NOT NULL,
    filename                        CHARACTER VARYING       	NOT NULL,
    content_type                    CHARACTER VARYING       	NOT NULL,
    path                            CHARACTER VARYING       	NOT NULL,
    thumbnail                       BYTEA                   	NOT NULL,

    PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE IF EXISTS Files;
DROP TABLE IF EXISTS Users;
