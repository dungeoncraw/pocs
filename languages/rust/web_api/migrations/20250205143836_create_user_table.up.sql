-- Add up migration script here
create table if not exists users(
    id  serial primary key,
    email varchar(255) not null,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    password varchar(255) not null,
    balance bigint not null default 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null
);