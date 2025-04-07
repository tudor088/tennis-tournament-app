create table if not exists user_tournaments
(
    user_id       bigint not null,
    tournament_id bigint not null,
    constraint FKa0x9bmn36n48hxh5fsemv2j15
        foreign key (tournament_id) references tournament (id),
    constraint FKpcbnkqjs8tftc5fox9u881fsm
        foreign key (user_id) references user (id)
);

