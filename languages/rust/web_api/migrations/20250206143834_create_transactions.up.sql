-- Add up migration script here
create table if not exists transactions(
    id  serial primary key,
    user_id int REFERENCES users,
    category_id int REFERENCES categories,
    type varchar(255) not null,
    description varchar(255) not null,
    memo varchar(255) not null,
    amount bigint not null default 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);