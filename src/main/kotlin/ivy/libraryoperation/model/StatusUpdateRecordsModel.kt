package ivy.libraryoperation.model

import java.sql.Date


data class StatusUpdateRecordsModel(

    val date: Date,

    val bookId: Int,

    val bookName: String,

    val memberId: Int,

    val memberLoginId: String,

    val isReturned: Boolean,

)
