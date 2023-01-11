package ivy.libraryoperation.model;

import java.sql.Date

data class PurchaseBookHistoryInfo(

    val date: Date,

    val bookId: Int,

    val bookName: String,

    val author: String,

    val price: Int,

    var totalBalance: Int = 20000

)
