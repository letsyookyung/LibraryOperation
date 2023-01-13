package ivy.libraryoperation.model;

data class BookInfoModel(
        val name: String,

        val author: String?,

        var isAvailableToCheckOut: Boolean = true
) {

        var price: Int = 0
}