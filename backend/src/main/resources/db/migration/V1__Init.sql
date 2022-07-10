create table rooms
(
    id   bigserial
        primary key,
    name varchar(255)
);


create table users
(
    id         bigserial
        primary key,
    is_enabled boolean      not null,
    name       varchar(255) not null
);


create table credentials
(
    id       bigserial
        constraint credentials_pk
            primary key,
    email    varchar(320) not null,
    password varchar(255) not null,
    user_id  bigint
        constraint credentials_users_id_fk
            references users
);


create table members
(
    id      bigserial
        primary key,
    user_id bigint not null
        constraint fkpj3n6wh5muoeakc485whgs3x5
            references users,
    room_id bigint not null
        constraint fkfmn2njuvf8gp0wqos6bp9enwc
            references rooms
);


create table member_role
(
    member_id bigint not null
        constraint fksfifma9lsmoaw3kx1ou2phk8u
            references members,
    roles     varchar(255)
);


create table tasks
(
    id               bigserial
        constraint tasks_pk
            primary key,
    title            varchar(255) not null,
    content          text,
    deadline         date         not null,
    budget           integer      not null,
    status           varchar(40)  not null,
    room_id          bigint       not null
        constraint tasks_rooms_id_fk
            references rooms,
    author_user_id   bigint       not null
        constraint tasks_author_users_id_fk
            references users,
    assignee_user_id bigint
        constraint tasks_assignee_user_id_id_fk
            references users
);


create table teams_conversations
(
    id                         varchar(255) not null
        constraint teams_conversations_pk
            primary key,
    raw_conversation_reference text         not null,
    user_id                    bigint       not null
        constraint teams_conversations_users_id_fk
            references users
);


create table teams_users
(
    id               varchar(255) not null
        constraint teams_user_pk
            primary key,
    name             text         not null,
    "aadObjectId"    text,
    email            varchar(320),
    original_user_id bigint       not null
        constraint teams_users_users_id_fk
            references users
);


create table usos_tokens
(
    id           uuid                     not null
        constraint usos_tokens_pk
            primary key,
    key          varchar                  not null,
    secret       varchar                  not null,
    created_date timestamp with time zone not null
);


create index usos_tokens_key_index
    on usos_tokens (key);

create table wallets
(
    id           bigserial
        primary key,
    coins_amount integer not null,
    name         varchar(255),
    owner_id     bigint  not null
        constraint fkd516f3xp7i3dfxiysb07g76wi
            references users
);


INSERT INTO public.rooms (id, name)
VALUES (1, 'Global');
