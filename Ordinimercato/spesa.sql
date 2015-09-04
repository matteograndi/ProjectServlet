DROP TABLE IF EXISTS utenti;
DROP TABLE IF EXISTS prodotti;
DROP TABLE IF EXISTS ordini;
DROP TABLE IF EXISTS listeprod;



CREATE TABLE utenti (
  Username varchar not null,
  Password varchar not null,
  Nome varchar not null,
  Address varchar not null,
  Phone varchar not null,
  Mail varchar not null,
  Dataul varchar,
  PRIMARY KEY  (Username) 
) ;

CREATE TABLE prodotti (
  Nome varchar not null,
  Descrizione varchar not null,
  Prezzo float,
  Immagine varchar,
  PRIMARY KEY  (Nome) 
 );

CREATE TABLE ordini (
  Id_ordine integer not null ,
  Utente varchar not null ,
  Prezzo float not null ,
  Dataor varchar,
  PRIMARY KEY  (Id_ordine) 
)  ;

CREATE TABLE listeprod(
  id_ordine integer, 
  prodotto varchar, 
  quantità integer, 
  Costo float,
  PRYMARY KEY (Id_ordine)
 );



INSERT INTO UTENTI VALUES('Skizzo','skizzo','Erik Bertoletti','Vivolo Stretto 11','333/3636366','schizzo84@gmail.com','1998-09-02')
INSERT INTO UTENTI VALUES('G3n1u5','ciriciao','Dario Fontana','via della Fricca','4375893764','geniusfonte@gmail.com','1998-09-02')
INSERT INTO PRODOTTI VALUES('Fragole','Vaschette da 100 g.',3.50,'fragole.jpg')
INSERT INTO PRODOTTI VALUES('Banane','Vaschette da 100 g.',5.50,'banane.jpg')
INSERT INTO PRODOTTI VALUES('Pasta','Confezione da 100 g.',3.00,'pasta.jpg')
