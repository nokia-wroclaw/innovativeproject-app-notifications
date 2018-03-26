CREATE TABLE "Nokia_App".notification 
(
  NotificationID bigserial NOT NULL, 
  UserID         int4 NOT NULL, 
  SourceID       int4 NOT NULL, 
  Flag           bool NOT NULL, 
  Topic          varchar(255) NOT NULL, 
  Message        text, 
  Time           timestamp default current_timestamp NOT NULL, 
  Priority       int2 NOT NULL, 
  PRIMARY KEY (NotificationID))
  WITH (
    OIDS = FALSE
);
CREATE TABLE "Nokia_App".subscription (
  UserID   int4 NOT NULL, 
  SourceID int4 NOT NULL,
  Name varchar(255) NOT NULL)
  WITH (
    OIDS = FALSE
);
CREATE TABLE "Nokia_App".user (
  UserID   serial NOT NULL, 
  Name     varchar(60) NOT NULL, 
  Surname  varchar(60) NOT NULL, 
  Login    varchar(60) NOT NULL, 
  Password varchar(60) NOT NULL, 
  PRIMARY KEY (UserID))
  WITH (
    OIDS = FALSE
);
ALTER TABLE "Nokia_App".subscription ADD CONSTRAINT FKsusbscript937025 FOREIGN KEY (UserID) REFERENCES "Nokia_App".user (UserID);
ALTER TABLE "Nokia_App".notification ADD CONSTRAINT FKnotificati473455 FOREIGN KEY (UserID) REFERENCES "Nokia_App".user (UserID);
ALTER TABLE "Nokia_App".subscription ADD CONSTRAINT FKsusbscript937026 FOREIGN KEY (UserID) REFERENCES "Nokia_App".user (UserID);

ALTER TABLE "Nokia_App".notification
    OWNER to postgres;
ALTER TABLE "Nokia_App".subscription
    OWNER to postgres;
ALTER TABLE "Nokia_App".user
    OWNER to postgres;