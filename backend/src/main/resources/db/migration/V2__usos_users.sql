UPDATE rooms
SET name = 'PW'
WHERE id = 'a6041b05-ebb9-4ff0-9b6b-d915d573afb2';

alter table users
    add role varchar(255) default 'USER' not null;

create table usos_users
(
    id         varchar(32)
        constraint usos_users_pk
            primary key,
    first_name varchar not null,
    last_name  varchar not null,
    email      varchar(254),
    user_id    uuid    not null
        constraint usos_user_users_id_fk
            references users
);
