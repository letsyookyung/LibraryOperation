package ivy.libraryoperation.model;

data class BookInfoModel(
        val name: String,

        val author: String?,

        var checkOutStatus: Boolean = false
) {

        var price: Int = 0
}