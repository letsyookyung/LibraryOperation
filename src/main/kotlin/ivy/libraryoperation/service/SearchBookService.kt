package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import ivy.libraryoperation.model.SearchInfoModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class SearchBookService(val db: JdbcTemplate) {

    fun findBookList(findOnlyAvailable: Boolean): List<BookInfoModel> {
        var result = emptyList<BookInfoModel>()

        when (findOnlyAvailable) {
            true -> {
                result = db.query("select * from bookList where isAvailableToCheckOut = true")
                { response, _ -> BookInfoModel(
                    response.getString("bookName"),
                    response.getString("author"),
                    response.getInt("price"),
                    response.getBoolean("isAvailableToCheckOut"))
                }
            }

            false -> {
                result = db.query("select bookName, author, price, isAvailableToCheckOut from bookList")
                { response, _ -> BookInfoModel(
                    response.getString("bookName"),
                    response.getString("author"),
                    response.getInt("price"),
                    response.getBoolean("isAvailableToCheckOut"))
                }
            }
        }
        return result
    }


    fun byAllInfo(searchInfo: SearchInfoModel) : List<BookInfoModel> {
        val query = "select * from bookList where bookName = '${searchInfo.bookName}' and author = '${searchInfo.author}'"

        val searchedList = db.query(query)
        { response, _ -> BookInfoModel(
                response.getString("bookName"),
                response.getString("author"),
                response.getInt("price"),
                response.getBoolean("isAvailableToCheckOut"),
            )
        }
        return searchedList
    }

    fun byBookName(bookName: String?) : List<BookInfoModel> {
        val query = "select * from bookList where bookName = '${bookName}'"

        val searchedList = db.query(query)
        { response, _ ->
            BookInfoModel(
                response.getString("bookName"),
                response.getString("author"),
                response.getInt("price"),
                response.getBoolean("isAvailableToCheckOut"),
            )
        }
        return searchedList
    }

    fun byAuthor(author: String?) : List<BookInfoModel> {
        val searchedList = db.query("select * from bookList where author = '${author}'")
        { response, _ ->
            BookInfoModel(
                response.getString("bookName"),
                response.getString("author"),
                response.getInt("price"),
                response.getBoolean("isAvailableToCheckOut"),
            )
        }
        return searchedList
    }

}