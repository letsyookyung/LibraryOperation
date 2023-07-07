create table if not exists Library.bookList
(
    bookName           text    not null,
    author         text    not null,
    price   int     not null,
    isAvailableToCheckOut BOOLEAN not null
);

create table if not exists Library.statusUpdateRecords
(
    date     datetime not null,
    bookId   int      not null,
    bookName   text      not null,
    memberId    int      not null,
    memberLoginId   text      not null,
    isReturned    BOOLEAN null

);

create table if not exists Library.members
(
    memberId int auto_increment,
    loginId   text not null,
    password  text not null,
    constraint memebers_pk
        primary key (memberId)
);

create table if not exists Library.managers
(
    managerId int auto_increment,
    loginId   text not null,
    password  text not null,
    constraint managers_pk
        primary key (managerId)
);

create table if not exists Library.purchaseHistory
(
    date     datetime not null,
    bookId   int auto_increment,
    bookName text     not null,
    author   text     not null,
    price    int      not null,
    remainingTotalBalance int    not null,
    constraint purchaseHistory_pk
        unique (bookId)
);

create table if not exists Library.totalBalance
(
    date    datetime not null,
    totalBalance int not null

);



