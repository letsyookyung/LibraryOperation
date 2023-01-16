package ivy.libraryoperation.model;
import java.sql.Date

data class PurchaseBookHistoryModel(

    val date: Date,

    val bookId: Int,

    val bookName: String,

    val author: String,

    val price: Int,

    val remainingTotalBalance: Int,

    )
