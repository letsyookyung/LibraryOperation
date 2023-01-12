package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UpdateBookListService (private val db: JdbcTemplate){
    fun checkOut(memberLoginId: String, bookInfo: BookInfoModel)  {
        val memberId = db.query("select memberId from members where loginId = $memberLoginId") {
                response, _ -> response.getInt("memberId")}[0]
        val bookId = db.query("select bookId from purchaseHistory where bookName = ${bookInfo.name}") {
                response,_ -> response.getInt("bookId")}[0] // 우선 중복되는 책 없다고 간주

        db.update("update bookList SET checkOutStatus = true where bookName = ${bookInfo.name}")
        db.update("insert into checkOutRecords (date, bookId, memberId) VALUES (${LocalDateTime.now()}, $bookId, $memberId)")

    }



}