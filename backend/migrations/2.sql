create table teams_conversations
(
    id                         varchar(255)
        constraint teams_conversations_pk
            primary key,
    raw_conversation_reference TEXT   not null,
    user_id                    bigint not null
        constraint teams_conversations_users_id_fk
            references users
);


create table teams_users
(
    id               varchar(255)
        constraint teams_user_pk
            primary key,
    name             TEXT   not null,
    "aadObjectId"    TEXT,
    email            varchar(320),
    original_user_id bigint not null
        constraint teams_users_users_id_fk
            references users
);

create table credentials
(
    id       bigint
        constraint credentials_pk
            primary key,
    email    varchar(320) not null,
    password varchar(255) not null,
    user_id  bigint
        constraint credentials_users_id_fk
            references users
);

create sequence credentials_id_seq;


alter table users
    drop column email;

alter table users
    drop column password;


alter table tasks
    rename column user_id to author_user_id;

alter table tasks
    add assignee_user_id bigint;

alter table tasks
    rename constraint tasks_users_id_fk to tasks_author_users_id_fk;

alter table tasks
    add constraint tasks_assignee_user_id_id_fk
        foreign key (assignee_user_id) references users;

