package ivy.libraryoperation.service

import ivy.libraryoperation.model.StatusUpdateRecordsModel
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

        val bookId = results[0]
        val memberId = results[1]

        db.update("update bookList SET isAvailableToCheckOut = false where bookName = '${bookName}'")
        db.update("insert into statusUpdateRecords (date, bookId, bookName, memberId, memberLoginId, isReturned) VALUES " +
                "('${LocalDateTime.now()}', ${bookId}, '${bookName}', ${memberId}, '${memberLoginId}', false)")

    }

    fun returnBook(memberId: Int, memberLoginId:String, bookId: Int, bookName: String) {
        try {
            db.update("update bookList SET isAvailableToCheckOut = true where bookName = '${bookName}'")
            db.update("insert into statusUpdateRecords (date, bookId, bookName, memberId, memberLoginId, isReturned) VALUES " +
                    "('${LocalDateTime.now()}', ${bookId}, '${bookName}', ${memberId}, '${memberLoginId}', true )")
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }
    }

    fun isCheckedOutById(memberId: Int, bookId: Int) : Boolean  {
        if (db.query("select * from statusUpdateRecords where memberId = '$memberId' and bookId = '$bookId' and isReturned = false")
            {response, _ -> response.getDate("date")}.size == 0) {
            return false
        }
        return true
    }

}