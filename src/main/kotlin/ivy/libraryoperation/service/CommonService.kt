package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.sql.SQLSyntaxErrorException

@Service
class CommonService(val db: JdbcTemplate) {

    fun isValidMember(loginId: String) : Boolean {
        try {
            if (db.query("select * from members where loginId = '${loginId}'") { response, _ ->
                    response.getString("memberId")}.size == 0)
                return false
        } catch (e: Exception) {
            println(e.message)
        }

        return true
    }

    fun isValidBook(bookName: String) : Boolean {
        try {
            if (db.query("select * from bookList where bookName = '${bookName}'") { response, _ ->
                    response.getString("bookName")}.size >= 1)
                return true
        } catch (e: SQLSyntaxErrorException) {
            println("error: ${e.message}")
            throw SQLSyntaxErrorException()
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }
        return false
    }

    fun isAvailableToCheckOut(mode: String, bookName: String) : Boolean {
        try {
            when (mode) {
                "checkOut" -> {
                    if (db.query("select * from bookList where bookName = '${bookName}' & isAvailableToCheckOut = 'true'") { response, _ ->
                            response.getString("bookName")}.size >= 1)
                        return true
                }
                "return" -> {
                    if (db.query("select * from bookList where bookName = '${bookName}' & isAvailableToCheckOut = 'false'") { response, _ ->
                            response.getString("bookName")}.size >= 1)
                        return true
                }

            }

        } catch (e: SQLSyntaxErrorException) {
            println("error: ${e.message}")
            throw SQLSyntaxErrorException()
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }
        return true
    }

    fun findMatchingNumberedID(memberLoginId: String, bookName: String) : MutableList<Int> {
        val results: MutableList<Int> = arrayListOf()
        results.add(db.query("select bookId from purchaseHistory where bookName = '${bookName}'") { response, _ ->
            response.getInt("bookId")}[0])  // 우선 중복되는 책 없다고 간주
        results.add(db.query("select memberId from members where loginId = '${memberLoginId}'") { response, _ ->
            response.getInt("memberId")}[0])
        return results
    }

    fun findBookList(findOnlyAvailable: Boolean) : List<BookInfoModel> {
        val result: List<BookInfoModel>

        when (findOnlyAvailable) {
            true -> {
                result= db.query("select bookName, author, isAvailableToCheckOut from bookList where isAvailableToCheckOut = false")
                { response, _ ->
                    BookInfoModel(
                        response.getString("bookName"),
                        response.getString("author"),
                        response.getBoolean("isAvailableToCheckOut")
                    )
                }
            }

            false -> {
                result = db.query("select bookName, author, isAvailableToCheckOut from bookList")
                { response, _ ->
                    BookInfoModel(
                        response.getString("bookName"),
                        response.getString("author"),
                        response.getBoolean("isAvailableToCheckOut")
                    )
                }
            }
        }
        return result
    }







}