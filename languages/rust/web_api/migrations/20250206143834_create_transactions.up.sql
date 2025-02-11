-- Add up migration script here
create table if not exists transactions(
    id  serial primary key,
    user_id int REFERENCES users not null,
    category_id int REFERENCES categories not null,
    type varchar(255) not null,
    description varchar(255),
    memo varchar(255) not null,
    amount bigint not null default 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null
);