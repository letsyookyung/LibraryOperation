create table if not exists Library.bookList
(
    id             int auto_increment,
    name           text    not null,
    author         text    null,
    price          int     not null,
    checkOutStatus BOOLEAN null,
    constraint id
        unique (id)
);

create table Library.checkOutList
(
    date     datetime not null,
    bookId   int      not null,
    memberId int      not null
);

