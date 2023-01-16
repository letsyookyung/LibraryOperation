package ivy.libraryoperation.service

import ivy.libraryoperation.model.StatusUpdateRecordsModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UpdateBookListService (
    private val db: JdbcTemplate,
    private val dataValidationService: DataValidationService
){
    fun checkOutBook(memberLoginId: String, bookName: String) {
        val results = dataValidationService.findMatchingNumberedID(memberLoginId, bookName)

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
        val query = "select * from statusUpdateRecords where memberId = '$memberId' and bookId = '$bookId' and isReturned = false"

        if (db.query(query) {response, _ -> response.getDate("date")}.size == 0) return false

        return true
    }


    fun findStatusUpdateRecords(): List<StatusUpdateRecordsModel> = db.query("select * from statusUpdateRecords")
    { response, _ ->
        StatusUpdateRecordsModel(
            response.getDate("date"),
            response.getInt("bookId"),
            response.getString("bookName"),
            response.getInt("memberId"),
            response.getString("memberLoginId"),
            response.getBoolean("isReturned"),
        )
    }


    fun findStatusUpdateRecordsById(memberLoginId: String): List<StatusUpdateRecordsModel> =
        db.query("select * from statusUpdateRecords where memberLoginId = '${memberLoginId}' order by date")
        { response, _ ->
            StatusUpdateRecordsModel(
                response.getDate("date"),
                response.getInt("bookId"),
                response.getString("bookName"),
                response.getInt("memberId"),
                response.getString("memberLoginId"),
                response.getBoolean("isReturned"),
            )
        }

}