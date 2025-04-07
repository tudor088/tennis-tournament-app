create table if not exists tournament
(
    id       bigint auto_increment
        primary key,
    date     date         null,
    location varchar(255) null,
    name     varchar(255) null
);

