-- Add up migration script here
create table if not exists categories(
    id  serial primary key,
    user_id int REFERENCES users,
    name varchar(255) not null,
    description varchar(255) not null,
    balance bigint not null default 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
