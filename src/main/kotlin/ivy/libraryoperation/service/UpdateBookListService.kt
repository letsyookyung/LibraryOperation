package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UpdateBookListService (
    private val db: JdbcTemplate,
    private val commonService: CommonService
){
    fun checkOutBook(memberLoginId: String, bookName: String) {
        val results = commonService.findMatchingNumberedID(memberLoginId, bookName)

        db.update("update bookList SET isAvailableToCheckOut = false where bookName = '${bookName}'")
        db.update("insert into statusUpdateRecords (date, bookId, memberId) VALUES ('${LocalDateTime.now()}', ${results[0]}, ${results[1]})")

    }

    fun returnBook(bookName: String, memberId: Int, bookId: Int) {

        try {
            db.update("update bookList SET isAvailableToCheckOut = true where bookName = '${bookName}'")
            db.update("insert into statusUpdateRecords (date, bookId, memberId) VALUES ('${LocalDateTime.now()}', ${memberId}, ${bookId})")
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }
    }

    fun isCheckedOutById(memberId: Int, bookId: Int) : Boolean  {
        println(memberId)
        println(bookId)
        println("select * from checkOutRecords where memberId = ${memberId} and bookId = ${bookId}")
        println(db.query("select * from statusUpdateRecords where memberId = ${memberId} and bookId = ${bookId}")
        {response, _ -> response.getInt("bookId")})
        if (db.query("select * from statusUpdateRecords where memberId = ${memberId} and bookId = ${bookId}")
            {response, _ -> response.getDate("date")}.size == 0) {
            return false
        }
        return true
    }

}