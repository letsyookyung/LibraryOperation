package ivy.libraryoperation.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseModel (

//    @JsonProperty("error-message")
//    var errorMessage: String? = null,

    @JsonProperty("result")
    var result: Any? = "success!",

)

