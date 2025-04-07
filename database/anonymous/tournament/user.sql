create table if not exists user
(
    id       bigint auto_increment
        primary key,
    email    varchar(255)                        not null,
    password varchar(255)                        not null,
    role     enum ('ADMIN', 'PLAYER', 'REFEREE') null,
    username varchar(255)                        not null,
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint UKsb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

