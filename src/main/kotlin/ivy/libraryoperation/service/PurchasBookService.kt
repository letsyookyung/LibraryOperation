package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfo

class PurchasBookService(val db: JdbcTemplate) {

    fun save(bookInfo: BookInfo) {
        db.update("insert into  values (?, ?, ?, ")
    }


}