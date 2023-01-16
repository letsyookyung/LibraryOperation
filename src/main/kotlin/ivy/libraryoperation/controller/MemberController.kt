package ivy.libraryoperation.controller

import io.swagger.v3.oas.annotations.Operation
import ivy.libraryoperation.model.ResponseModel
import ivy.libraryoperation.model.SearchInfoModel
import ivy.libraryoperation.model.StatusUpdateRecordsModel
import ivy.libraryoperation.service.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/member")
class MemberController(
    private val dataValidationService: DataValidationService,
    private val updateBookListService: UpdateBookListService,
    private val searchBookService: SearchBookService,
) {

    @PostMapping("/get-my-status-update-records")
    fun findStatusUpdateRecordById(@RequestParam memberLoginId: String) : List<StatusUpdateRecordsModel> = updateBookListService.findStatusUpdateRecordsById(memberLoginId)


    @PostMapping("/search-book/")
    @Operation(description = "You can write book name and author name together or either one of them.")
    fun searchBook(@RequestBody searchInfo: SearchInfoModel) : Any {

        if (searchInfo.bookName?.isNotEmpty() == true && searchInfo.author?.isNotEmpty() == true) {
            if (!dataValidationService.isValidBook(searchInfo.bookName!!)) return ResponseModel(
                false,
                "해당 제목의 도서없음"
            )
            return searchBookService.byAllInfo(searchInfo)
        } else if (searchInfo.bookName?.isNotEmpty() == true) {
            if (!dataValidationService.isValidBook(searchInfo.bookName!!)) return ResponseModel(
                false,
                "해당 제목의 도서없음"
            )
            return searchBookService.byBookName(searchInfo.bookName)
        } else if (searchInfo.author?.isNotEmpty() == true) {
            if (!dataValidationService.isValidAuthor(searchInfo.author!!)) return ResponseModel(
                false,
                "해당 저자명의 도서없음"
            )
            return searchBookService.byAuthor(searchInfo.author)
        }

        return ResponseModel(false, "도서명 혹은 저자명 중 하나는 입력해야 함.")

    }


}