package ivy.libraryoperation.model;

data class BookInfoModel(
        val name: String,

        val author: String,

        var price: Int,

        var checkOutStatus: Boolean = false
)