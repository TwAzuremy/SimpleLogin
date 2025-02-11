create database if not exists simpleLogin;

create table user
(
    id       bigint primary key auto_increment,
    username varchar(64)  not null unique,
    password char(96)     null,
    name     varchar(64)  null,
    email    varchar(255) not null unique,
    profile  varchar(255) null,
    constraint email_format CHECK (email REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$')
);

create table oauth2_user
(
    id          CHAR(36) PRIMARY KEY DEFAULT UUID(),
    user_id     bigint       null,
    provider    varchar(255) not null,
    provider_id varchar(255) not null,
    username    varchar(64)  null unique,
    email       varchar(255) null,
    profile     varchar(255) null,
    foreign key (user_id) references user (id)
);