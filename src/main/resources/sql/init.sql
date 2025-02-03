create database if not exists simpleLogin;

create table user
(
    id       int primary key auto_increment,
    username varchar(64)  not null,
    password char(96)     not null,
    email    varchar(255) not null unique,
    profile  varchar(255) default 'This user does not have a profile.',
    constraint email_format CHECK (email REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$')
);