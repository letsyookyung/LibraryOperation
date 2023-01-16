package ivy.libraryoperation.service

import ivy.libraryoperation.controller.ManagerController
import ivy.libraryoperation.model.BookInfoModel
import ivy.libraryoperation.model.PurchaseBookHistoryModel
import ivy.libraryoperation.model.ResponseModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PurchaseBookService (private val db: JdbcTemplate) {

    fun depositIntoAccount(deposit: Int) {
        val today = LocalDateTime.now()
        db.update("insert into totalBalance values (?, ?) ", today, deposit)
    }


    fun purchaseBook(bookInfo: BookInfoModel, priorTotalBalance: Int): ResponseModel {
        val today = LocalDateTime.now()

        addInPurchaseHistory(bookInfo, today, priorTotalBalance)
        addInBookList(bookInfo)

        return ResponseModel(true, bookInfo.toString())
    }


    fun findPurchaseHistory() : List<PurchaseBookHistoryModel> =
        db.query("select * from purchaseHistory")
        {response, _ -> PurchaseBookHistoryModel(
            response.getDate("date"),
            response.getInt("bookId"),
            response.getString("bookName"),
            response.getString("author"),
            response.getInt("price"),
            response.getInt("remainingTotalBalance"))
        }


    fun addInPurchaseHistory(bookInfo: BookInfoModel, today: LocalDateTime, priorTotalBalance: Int) {
        val query = "insert into purchaseHistory (date, bookName, author, price, remainingTotalBalance) values " +
        "('${today}', '${bookInfo.name}', '${bookInfo.author}', ${bookInfo.price}, ${priorTotalBalance}-${bookInfo.price})"

        db.update(query)

        depositIntoAccount(priorTotalBalance - bookInfo.price)
    }

    fun addInBookList(bookInfo: BookInfoModel) = db.update(
        "insert into bookList (bookName, author, price, isAvailableToCheckOut) values " +
                "('${bookInfo.name}', '${bookInfo.author}', '${bookInfo.price}', ${bookInfo.isAvailableToCheckOut})")


}