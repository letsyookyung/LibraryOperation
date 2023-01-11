package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfo
import ivy.libraryoperation.model.PurchaseBookHistoryInfo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PurchaseBookService (val db: JdbcTemplate) {

    fun addInPurchaseHistory(bookInfo: BookInfo) {
        db.update("insert into purchaseHistory (date, bookName, author, price) values " +
                "('${LocalDateTime.now()}', '${bookInfo.name}', '${bookInfo.author}', ${bookInfo.price})")

//        purchaseBookHistoryInfo.totalBalance -= bookInfo.price
    }

    fun addInBookList(bookInfo: BookInfo) {
        db.update("insert into bookList (bookName, author, checkOutStatus) values " +
                "('${bookInfo.name}', '${bookInfo.author}', ${bookInfo.checkOutStatus})")
    }

    fun findPurchaseHistory() : List<PurchaseBookHistoryInfo> = db.query(
        "select * from purchaseHistory") {response, _ ->
        PurchaseBookHistoryInfo(response.getDate("date"),
                                response.getInt("bookId"),
                                response.getString("bookName"),
                                response.getString("author"),
                                response.getInt("price")
        )
    }


}