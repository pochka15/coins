create table rooms
(
    id   uuid primary key,
    name varchar(255)
);


create table users
(
    id         uuid primary key,
    is_enabled boolean      not null,
    name       varchar(255) not null,
    email      varchar(254) not null
);


create table members
(
    id      bigserial
        primary key,
    user_id uuid not null
        constraint members_users_fk
            references users,
    room_id uuid not null
        constraint members_rooms_fk
            references rooms
);


create table member_role
(
    member_id bigint not null
        constraint member_role_members_fk
            references members,
    roles     varchar(255)
);


create table tasks
(
    id               uuid
        constraint tasks_pk primary key,
    title            varchar(255)             not null,
    content          text,
    deadline         date                     not null,
    creation_date    timestamp with time zone not null,
    budget           integer                  not null,
    status           varchar(40)              not null,
    room_id          uuid                     not null
        constraint tasks_rooms_fk
            references rooms,
    author_user_id   uuid                     not null
        constraint tasks_author_users_id_fk
            references users,
    assignee_user_id uuid
        constraint tasks_assignee_user_id_id_fk
            references users
);


create table usos_tokens
(
    id           uuid                     not null
        constraint usos_tokens_pk
            primary key,
    key          varchar                  not null,
    secret       varchar                  not null,
    created_date timestamp with time zone not null,
    user_id      uuid                     not null
        constraint usos_tokens_users_id_fk references users
);


create index usos_tokens_key_index
    on usos_tokens (key);

create table wallets
(
    id           uuid
        primary key,
    coins_amount integer not null,
    name         varchar(255),
    owner_id     uuid    not null
        constraint fkd516f3xp7i3dfxiysb07g76wi
            references users
);


INSERT INTO public.rooms (id, name)
VALUES ('a6041b05-ebb9-4ff0-9b6b-d915d573afb2', 'Global');
