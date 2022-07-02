-- auto-generated definition
create table users
(
    id         bigserial
        primary key,
    email      varchar(255),
    is_enabled boolean      not null,
    name       varchar(255) not null,
    password   varchar(255) not null
);

-- auto-generated definition
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


-- auto-generated definition
create table rooms
(
    id   bigserial
        primary key,
    name varchar(255)
);

-- auto-generated definition
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


-- auto-generated definition
create table member_role
(
    member_id bigint not null
        constraint fksfifma9lsmoaw3kx1ou2phk8u
            references members,
    roles     varchar(255)
);

CREATE TABLE system_version
(
    id      int4         NOT NULL,
    version varchar(100) NOT NULL,
    CONSTRAINT AK_VERSION_SYSTEM_V UNIQUE (version) NOT DEFERRABLE INITIALLY IMMEDIATE,
    CONSTRAINT system_version_pk PRIMARY KEY (id)
);

update system_version
set version = '1';

create table tasks
(
    id       bigserial
        constraint tasks_pk
            primary key,
    title    varchar(255) not null,
    content  TEXT,
    deadline date         not null,
    budget   int          not null,
    status   varchar(40)  not null,
    room_id  bigint       not null
        constraint tasks_rooms_id_fk
            references rooms,
    user_id  bigint       not null
);

alter table tasks
    add constraint tasks_users_id_fk
        foreign key (author_user_id) references users;

