create table if not exists client_category
(
    id   int auto_increment
        primary key,
    name varchar(255) not null
);

create table if not exists salesman
(
    id                int auto_increment
        primary key,
    email_address     varchar(100) not null,
    first_name        varchar(100) not null,
    last_name         varchar(100) not null,
    lat_home_address  double       not null,
    long_home_address double       not null,
    password          varchar(255) not null
);

create table if not exists client
(
    id                 int auto_increment
        primary key,
    company_name       varchar(255)                not null,
    contact_first_name varchar(100)                null,
    contact_last_name  varchar(100)                null,
    description        varchar(1000)               null,
    lat_home_address   double                not null,
    long_home_address  double                not null,
    phone_number       varchar(255)                null,
    salesman_id        int                         null,
    client_category_id int                         null,
    constraint fk_client_category_id
        foreign key (client_category_id) references client_category (id),
    constraint fk_salesman_id
        foreign key (salesman_id) references salesman (id)
);

