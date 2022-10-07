INSERT INTO "role"
values (101, 'ROLE_ADMIN');
INSERT INTO "role"
values (102, 'ROLE_USER');
INSERT INTO "role"
values (103, 'ROLE_UNDEFINED');

INSERT INTO "users"
values (1, 'oleg', 'Oleg', 'Tokarev', 'oleg@mail.ru', '$2a$12$jegcrhOb9pDtXdIGKpYB1ervHNx6yiXoNSIL0bBrhfEOVJhbXx31i',
        current_timestamp, current_timestamp, 101);
INSERT INTO "users"
values (2, 'misha', 'Misha', 'Voronkov', 'misha@mail.ru',
        '$2a$12$jegcrhOb9pDtXdIGKpYB1ervHNx6yiXoNSIL0bBrhfEOVJhbXx31i', current_timestamp, current_timestamp, 102);