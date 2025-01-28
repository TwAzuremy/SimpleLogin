create database if not exists simpleLogin;

create table user
(
    id       int primary key auto_increment,
    username varchar(64)  not null,
    password char(96)     not null,
    email    varchar(255) not null,
    constraint email_format CHECK (email REGEXP '^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$')
);