
package ivy.libraryoperation.model;

import java.time.LocalDateTime

data class HistoryByIdModel(
    var date: LocalDateTime,

    val book: String,

    val lastStatus: String
)
