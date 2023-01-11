
package ivy.libraryoperation.model;

import java.time.LocalDateTime

data class HistoryByIdInfo(
    var date: LocalDateTime,

    val book: String,

    val lastStatus: String
)
