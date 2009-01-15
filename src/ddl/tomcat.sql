create table if not exists tomcat_users (
    user varchar(15) not null primary key,
    pass varchar(15) not null
);

create table if not exists tomcat_roles (
    user varchar(15) not null,
    role varchar(15) not null,
    unique(user, role)
);

insert into tomcat_users values ('bbteam', 'somepassword');
insert into tomcat_roles values ('bbteam', 'manager');
insert into tomcat_roles values ('bbteam', 'bbservice-admin');
