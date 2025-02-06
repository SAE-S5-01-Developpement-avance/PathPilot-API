create table if not exists client_category
(
    id   int auto_increment primary key,
    name varchar(10) not null
);

create table if not exists salesman
(
    id                int auto_increment primary key,
    email_address     varchar(100) not null,
    first_name        varchar(100) not null,
    last_name         varchar(100) not null,
    lat_home_address  double       not null,
    long_home_address double       not null,
    password          varchar(255) not null
);

create table if not exists client
(
    id                 int auto_increment primary key,
    company_name       varchar(255)                not null,
    contact_first_name varchar(100)                null,
    contact_last_name  varchar(100)                null,
    description        varchar(255)               null,
    lat_home_address   double                not null,
    long_home_address  double                not null,
    phone_number       varchar(20)                null,
    salesman_id        int                         null,
    client_category_id int                         null DEFAULT 1,
    constraint fk_client_category_id
        foreign key (client_category_id) references client_category (id),
    constraint fk_salesman_id
        foreign key (salesman_id) references salesman (id)
);

INSERT INTO client_category (id, name) VALUES (1,'CLIENT');
INSERT INTO client_category (id, name) VALUES (2, 'PROSPECT');