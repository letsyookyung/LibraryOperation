package ivy.libraryoperation.model;

data class BookInfoModel(

        val name: String = "",

        val author: String? = "",

        var price: Int = 0,

        var isAvailableToCheckOut: Boolean = true

)
