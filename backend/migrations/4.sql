create table usos_tokens
(
    id           uuid
        constraint usos_tokens_pk
            primary key,
    key          varchar                  not null,
    secret       varchar                  not null,
    created_date timestamp with time zone not null
);

create index usos_tokens_key_index
    on usos_tokens (key);

