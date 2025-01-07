create sequence if not exists profile_user_seq
    start with 1
    increment by 1
    no minvalue
    no maxvalue
    cache 1;

create table if not exists profile_users (
    id         bigint primary key default nextval('profile_user_seq'),
    fio        varchar,
    age        bigint,
    email      varchar unique,
    phone      varchar unique,
    username   varchar not null unique,
    password   varchar not null,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
)