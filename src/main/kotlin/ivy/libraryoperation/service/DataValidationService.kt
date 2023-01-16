package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import ivy.libraryoperation.model.EnrollInfoModel
import ivy.libraryoperation.model.StatusUpdateRecordsModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.sql.SQLSyntaxErrorException

@Service
class DataValidationService(val db: JdbcTemplate) {

    fun isValidManager(loginId: String): Boolean {
        try {
            if (db.query("select * from managers where loginId = '${loginId}'") { response, _ ->
                    response.getString("managerId")
                }.size == 0)
                return false
        } catch (e: Exception) {
            println(e.message)
        }

        return true
    }


    fun isValidMember(loginId: String): Boolean {
        try {
            val query = "select * from members where loginId = '${loginId}'"
            if (db.query(query) { response, _ -> response.getString("memberId")}.size == 0) return false
        } catch (e: Exception) {
            println(e.message)
        }

        return true
    }


    fun isValidBook(bookName: String): Boolean {
        try {
            val query = "select * from bookList where bookName = '${bookName}'"
            if (db.query(query) { response, _ -> response.getString("bookName") }.size >= 1) return true
        } catch (e: SQLSyntaxErrorException) {
            println("error: ${e.message}")
            throw SQLSyntaxErrorException()
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }
        return false
    }


    fun isValidAuthor(author: String): Boolean {
        try {
            val query = "select * from bookList where author = '${author}'"
            if (db.query(query) { response, _ -> response.getString("author") }.size >= 1) return true
        } catch (e: SQLSyntaxErrorException) {
            println("error: ${e.message}")
            throw SQLSyntaxErrorException()
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }
        return false
    }


    fun isAvailableToCheckOut(mode: String, bookName: String): Boolean {
        var query = ""
        try {
            when (mode) {
                "checkOut" -> {
                    query = "select * from bookList where bookName = '${bookName}' and isAvailableToCheckOut = true"
                    if (db.query(query) { response, _ -> response.getString("bookName")}.size >= 1) return true
                }

                "return" -> {
                    query = "select * from bookList where bookName = '${bookName}' and isAvailableToCheckOut = false"
                    if (db.query(query) { response, _ -> response.getString("bookName") }.size >= 1) return true
                }
            }
        } catch (e: SQLSyntaxErrorException) {
            println("error: ${e.message}")
            throw SQLSyntaxErrorException()
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }
        return false
    }


    fun findMatchingNumberedID(memberLoginId: String, bookName: String): MutableList<Int> {
        val results: MutableList<Int> = arrayListOf()
        var query = ""

        query = "select bookId from purchaseHistory where bookName = '${bookName}'"
        results.add(db.query(query) { response, _ -> response.getInt("bookId") }[0])  // 우선 중복되는 책 없다고 간주

        query = "select memberId from members where loginId = '${memberLoginId}'"
        results.add(db.query(query) { response, _ -> response.getInt("memberId") }[0])

        return results
    }


}
