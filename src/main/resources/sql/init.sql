create database if not exists simpleLogin;

create table user
(
    id       bigint primary key auto_increment,
    username varchar(64)  not null default 'User',
    password char(96)     null,
    email    varchar(255) not null unique,
    profile  varchar(255) null,
    constraint email_format CHECK (email REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$')
);

create table oauth2_user
(
    id          bigint primary key auto_increment,
    user_id     bigint       not null,
    provider    varchar(20)  not null,
    provider_id varchar(255) not null,
    foreign key (user_id) references user (id)
);