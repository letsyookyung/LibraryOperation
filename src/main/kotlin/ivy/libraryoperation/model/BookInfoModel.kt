package ivy.libraryoperation.model;

data class BookInfo(
        val name: String,

        val author: String,

        var price: Int,

        var checkOutStatus: Boolean = false
)