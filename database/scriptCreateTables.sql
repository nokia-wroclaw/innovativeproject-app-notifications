CREATE TABLE notifications.notification (
  NotificationID int4 NOT NULL, 
  UserID         int4 NOT NULL, 
  SourceID       int4 NOT NULL, 
  Flag           bool NOT NULL, 
  Topic          varchar(255) NOT NULL, 
  Message        text, 
  Time           timestamp NOT NULL, 
  Priority       int2 NOT NULL, 
  PRIMARY KEY (NotificationID))
  WITH (
    OIDS = FALSE
);
CREATE TABLE notifications.susbscription (
  UserID   int4 NOT NULL, 
  SourceID int4 NOT NULL)
  WITH (
    OIDS = FALSE
);
CREATE TABLE notifications.user (
  UserID   int4 NOT NULL, 
  Name     varchar(60) NOT NULL, 
  Surname  varchar(60) NOT NULL, 
  Login    varchar(60) NOT NULL, 
  Password varchar(60) NOT NULL, 
  PRIMARY KEY (UserID))
  WITH (
    OIDS = FALSE
);
ALTER TABLE notifications.susbscription ADD CONSTRAINT FKsusbscript937025 FOREIGN KEY (UserID) REFERENCES notifications.user (UserID);
ALTER TABLE notifications.notification ADD CONSTRAINT FKnotificati473455 FOREIGN KEY (UserID) REFERENCES notifications.user (UserID);
ALTER TABLE notifications.susbscription ADD CONSTRAINT FKsusbscript937026 FOREIGN KEY (UserID) REFERENCES notifications.user (UserID);

ALTER TABLE notifications.notification
    OWNER to postgres;
ALTER TABLE notifications.source
    OWNER to postgres;
ALTER TABLE notifications.susbscription
    OWNER to postgres;
ALTER TABLE notifications.user
    OWNER to postgres;