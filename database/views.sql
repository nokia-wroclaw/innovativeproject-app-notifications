CREATE VIEW "Nokia_App".user_notifications
AS select "user".userid, "subscription".name from "Nokia_App".subscription, "Nokia_App".user
WHERE "user".userid = "subscription".userid;


