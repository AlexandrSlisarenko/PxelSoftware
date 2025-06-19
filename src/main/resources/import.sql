
INSERT INTO pixel.users(date_of_birth, user_name, user_password) VALUES ('1990-06-20', 'ivanov_ivan', '{bcrypt}$2a$12$oMXCCGIM57txm7XqeXWue.hKfv8lZqD5S3v7LY0Q0zqK2Kne0N.SK');
INSERT INTO pixel.users(date_of_birth, user_name, user_password) VALUES ('1990-05-20', 'petrov_petr', '{bcrypt}$2a$12$xe6jIXeqhaypUJeNN/8qHOR5s4sIv08zLgJRxt.G8P3U2hJW2DINW');
INSERT INTO pixel.users(date_of_birth, user_name, user_password) VALUES ('1990-02-20', 'sidorov_sidor', '{bcrypt}$2a$12$PVKm3wFEqu3MYV0gtIpco.KCS4L/z8DlHEGCLSqcgrN/HJ0CPk7Bu');

INSERT INTO pixel.account(balance, id, user_id) values (10,1,1);
INSERT INTO pixel.account(balance, id, user_id) values (10,2,2);
INSERT INTO pixel.account(balance, id, user_id) values (10,3,3);

INSERT INTO pixel.email_data(id, user_id, email) VALUES (1, 1, 'ivanov_ivan@mail,ru');
INSERT INTO pixel.email_data(id, user_id, email) VALUES (2, 2, 'petrov_petr@mail,ru');
INSERT INTO pixel.email_data(id, user_id, email) VALUES (3, 3, 'sidorov_sidor@mail,ru');

INSERT INTO pixel.phone_data(id, user_id, phone) VALUES (1,1,'79207865432');
INSERT INTO pixel.phone_data(id, user_id, phone) VALUES (2,2,'79207865433');
INSERT INTO pixel.phone_data(id, user_id, phone) VALUES (3,3,'79207865434');


