package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import ivy.libraryoperation.model.PurchaseBookHistoryModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PurchaseBookService (private val db: JdbcTemplate) {

    fun depositIntoAccount(deposit: Int) {
        val today = LocalDateTime.now()
        db.update("insert into totalBalance values (?, ?) ", deposit, today)
    }


    fun purchaseBook(bookInfo: BookInfoModel) : BookInfoModel {
        val today = LocalDateTime.now()
        val priorTotalBalance = db.query("select totalBalance from totalBalance order by date desc limit 1")
        {response, _ -> response.getInt("totalBalance")}[0]

        if ((priorTotalBalance- bookInfo.price) <= 0) {
            throw RuntimeException("도서 가격 비쌈")
        }

        addInPurchaseHistory(bookInfo, today, priorTotalBalance)
        addInBookList(bookInfo)

        return bookInfo
    }


    fun findPurchaseHistory() : List<PurchaseBookHistoryModel> = db.query(
        "select * from purchaseHistory") {response, _ ->
        PurchaseBookHistoryModel(response.getDate("date"),
            response.getInt("bookId"),
            response.getString("bookName"),
            response.getString("author"),
            response.getInt("price"),
            response.getInt("remainingTotalBalance"))
    }


    fun addInPurchaseHistory(bookInfo: BookInfoModel, today: LocalDateTime, priorTotalBalance: Int) {
        db.update("insert into purchaseHistory (date, bookName, author, price, remainingTotalBalance) values " +
                "('${today}', '${bookInfo.name}', '${bookInfo.author}', ${bookInfo.price}, ${priorTotalBalance}-${bookInfo.price})")
        depositIntoAccount(priorTotalBalance - bookInfo.price)
    }

    fun addInBookList(bookInfo: BookInfoModel) = db.update(
        "insert into bookList (bookName, author, isAvailableToCheckOut) values " +
                "('${bookInfo.name}', '${bookInfo.author}', ${bookInfo.isAvailableToCheckOut})")



}