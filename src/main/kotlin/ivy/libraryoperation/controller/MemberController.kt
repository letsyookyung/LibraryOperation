package ivy.libraryoperation.controller

import io.swagger.v3.oas.annotations.Operation
import ivy.libraryoperation.model.ResponseModel
import ivy.libraryoperation.model.SearchInfoModel
import ivy.libraryoperation.service.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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

    companion object {
        private val NO_ID_ERROR_MESSAGE = "존재하지 않는 ID 입니다."
        private val BOOK_NAME_ERROR_MESSAGE = "해당 도서 없음"
        private val AUTHOR_NAME_ERROR_MESSAGE = "해당 저자명의 도서 없음"
        private val NOTHING_WRITTEN_ERROR_MESSAGE = "도서명 혹은 저자명 중 하나를 반드시 입력"
    }

    @GetMapping("/my-status-update-records")
    fun findStatusUpdateRecordById(@RequestParam memberLoginId: String): ResponseEntity<ResponseModel> {
        if (!dataValidationService.isValidMember(memberLoginId)) return ResponseEntity(ResponseModel(NO_ID_ERROR_MESSAGE), HttpStatus.OK) //체크

        val response = ResponseModel(updateBookListService.findStatusUpdateRecordsById(memberLoginId))

        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/search-book/")
    @Operation(description = "You can write book name and author name together or either one of them.")
    fun searchBook(@RequestBody searchInfo: SearchInfoModel): ResponseEntity<ResponseModel> {
        val response:Any
        val caseAll = searchInfo.bookName?.isNotEmpty() == true && searchInfo.author?.isNotEmpty() == true
        val caseOnlyBook = searchInfo.bookName?.isNotEmpty() == true && searchInfo.author?.isNotEmpty() == false
        val caseOnlyAuthor = searchInfo.bookName?.isNotEmpty() == false && searchInfo.author?.isNotEmpty() == true

        if (caseAll) {
            return if (dataValidationService.isValidBook(searchInfo.bookName!!)) {
                response = ResponseModel(searchBookService.byAllInfo(searchInfo))
                ResponseEntity(response, HttpStatus.OK)
            } else {
                ResponseEntity(ResponseModel(BOOK_NAME_ERROR_MESSAGE), HttpStatus.OK) // 체크
            }
        } else if (caseOnlyBook) {
            return if (dataValidationService.isValidBook(searchInfo.bookName!!)) {
                response = ResponseModel(searchBookService.byBookName(searchInfo.bookName))
                ResponseEntity(response, HttpStatus.OK)
            } else {
                ResponseEntity(ResponseModel(BOOK_NAME_ERROR_MESSAGE), HttpStatus.OK) // 체크
            }
        } else if (caseOnlyAuthor) {
            return if (dataValidationService.isValidAuthor(searchInfo.author!!)) {
                response = ResponseModel(searchBookService.byAuthor(searchInfo.author))
                ResponseEntity(response, HttpStatus.OK)
            } else {
                ResponseEntity(ResponseModel(AUTHOR_NAME_ERROR_MESSAGE), HttpStatus.OK) // 체크
            }
        } else {
            return ResponseEntity(ResponseModel(NOTHING_WRITTEN_ERROR_MESSAGE), HttpStatus.OK) // 체크
        }
    }


}
