create database if not exists simpleLogin;

create table user
(
    id       bigint primary key auto_increment,
    username varchar(64)  null unique,
    password char(96)     null,
    email    varchar(255) null unique,
    profile  varchar(255) null
);

create table oauth2_user
(
    id          bigint primary key auto_increment,
    user_id     bigint       not null,
    provider    varchar(255) not null,
    provider_id varchar(255) not null,
    foreign key (user_id) references user (id)
);