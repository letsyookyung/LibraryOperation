package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class CommonService(val db: JdbcTemplate) {

    fun isValidMember(loginId: String) : Boolean {
        if (db.query("select memberId from members where loginId = ${loginId}") { response, _ ->
                response.getString("memberId")}.size == 0) {
            return false
        }
        return true
    }

    fun isValidBook(bookName: String) : Boolean {
        if (db.query("select bookName from bookList where bookName = ${bookName}") { response, _ ->
                response.getString("bookName")
            }.size == 0) {
            return false
        }
    }

    fun findBookList(findOnlyAvailable: Boolean) : List<BookInfoModel> {
        val result: List<BookInfoModel>

        when (findOnlyAvailable) {
            true -> {
                result= db.query("select bookName, author, checkOutStatus from bookList where checkOutStatus = false")
                { response, _ ->
                    BookInfoModel(
                        response.getString("bookName"),
                        response.getString("author"),
                        response.getBoolean("checkOutStatus")
                    )
                }
            }

            false -> {
                result = db.query("select bookName, author, checkOutStatus from bookList")
                { response, _ ->
                    BookInfoModel(
                        response.getString("bookName"),
                        response.getString("author"),
                        response.getBoolean("checkOutStatus")
                    )
                }
            }
        }
        return result
    }







}