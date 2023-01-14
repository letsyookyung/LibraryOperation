package ivy.libraryoperation.service

import ivy.libraryoperation.model.BookInfoModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class SearchBookService(val db: JdbcTemplate) {

    fun byBookName(bookName: String?) : List<BookInfoModel> {

        val searchedList = db.query("select * from bookList where bookName = '${bookName}'")
        { response, _ ->
            BookInfoModel(
                response.getString("bookName"),
                response.getString("author"),
                response.getInt("price"),
                response.getBoolean("isAvailableToCheckOut"),
            )
        }

//        if (searchedList.size == 1)

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