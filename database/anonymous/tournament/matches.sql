create table if not exists matches
(
    id            bigint auto_increment
        primary key,
    completed     bit          not null,
    score         varchar(255) null,
    player1_id    bigint       null,
    player2_id    bigint       null,
    referee_id    bigint       null,
    tournament_id bigint       null,
    constraint FK1ca9foppvbyaup2ivevglq258
        foreign key (referee_id) references user (id),
    constraint FK6u6jn45m2juuk50mg6hxn71p7
        foreign key (tournament_id) references tournament (id),
    constraint FKb1i2edn5ex45366wfyu1jells
        foreign key (player2_id) references user (id),
    constraint FKqxkqultpw30usovw7nqa2qraj
        foreign key (player1_id) references user (id)
);

