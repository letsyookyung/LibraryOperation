package ivy.libraryoperation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import ivy.libraryoperation.model.*
import ivy.libraryoperation.service.CommonService
import ivy.libraryoperation.service.PurchaseBookService
import ivy.libraryoperation.service.SearchBookService
import ivy.libraryoperation.service.UpdateBookListService

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.dao.DuplicateKeyException
import java.sql.SQLSyntaxErrorException


@RestController
class LibraryOperationController (
    private val db: JdbcTemplate,
    private val purchaseBookService: PurchaseBookService,
    private val commonService: CommonService,
    private val updateBookListService: UpdateBookListService,
    private val searchBookService: SearchBookService,
) {

    data class ResponseModel(val isSuccess: Boolean? = false, val result: String?)

    @Tag(name = "MANAGER MODE", description = "Only available for managers")
    @PostMapping("/manager/enroll")
    @Operation(description = " Write 'manager' or 'member' in *type* field. ")
    fun enroll(@RequestBody enrollInfo: EnrollInfoModel) : ResponseModel {
        try {
            when (enrollInfo.type) {
                "manager" -> {
                    if (commonService.isValidManager(enrollInfo.loginId)) return ResponseModel(false, "아이디중복")
                    db.update("insert into managers (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")
                }
                "member" -> {
                    if (commonService.isValidMember(enrollInfo.loginId)) return ResponseModel(false, "아이디중복")
                    db.update("insert into members (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")
                }
            }
        } catch (e: SQLSyntaxErrorException) {
            println("error: ${e.message}")
            throw SQLSyntaxErrorException()
        } catch (e: DuplicateKeyException) {
            println("error: $e")
            return ResponseModel(false, "중복된 아이디")
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }

        return ResponseModel(true, "아이디 저장함")
    }

    @Tag(name = "MANAGER MODE")
    @GetMapping("/manager/get-book-list")
    fun findBookList(@RequestParam findOnlyAvailable: Boolean) : List<BookInfoModel> = commonService.findBookList(findOnlyAvailable)

    @Tag(name = "MANAGER MODE")
    @GetMapping("/manager/get-status-update-records")
    fun findStatusUpdateRecords() : List<StatusUpdateRecordsModel> = commonService.findStatusUpdateRecords()

    @Tag(name = "MANAGER MODE")
    @PostMapping("/manager/update-book-list/checkOut")
    fun checkOutBook(@RequestParam memberLoginId: String, bookName: String) : ResponseModel {
        val mode = "checkOut"

        if (!commonService.isValidMember(memberLoginId)) return ResponseModel(false, "id없음")

        if (!commonService.isValidBook(bookName)) return ResponseModel(false, "도서없음")

        if (!commonService.isAvailableToCheckOut(mode, bookName)) return ResponseModel(false, "대여중임")

        updateBookListService.checkOutBook(memberLoginId, bookName)

        return ResponseModel(true, "$memberLoginId 님, $bookName 대여 완료")
    }

    @Tag(name = "MANAGER MODE")
    @PostMapping("/manager/update-book-list/return")
    fun returnBook(@RequestParam memberLoginId: String, bookName: String) : ResponseModel {
        val mode = "return"

        if (!commonService.isValidMember(memberLoginId)) return ResponseModel(false, "id없음")

        if (!commonService.isValidBook(bookName)) return ResponseModel(false, "도서없음")

        if (!commonService.isAvailableToCheckOut(mode, bookName)) return ResponseModel(false, "대여중이 아님")

        val bookId = commonService.findMatchingNumberedID(memberLoginId, bookName)[0]
        val memberId = commonService.findMatchingNumberedID(memberLoginId, bookName)[1]

        if (!updateBookListService.isCheckedOutById(memberId, bookId)) return ResponseModel(false, "해당 도서를 대여하고 있지 않음")

        updateBookListService.returnBook(memberId, memberLoginId, bookId, bookName)

        return ResponseModel(true, "$memberLoginId 님, $bookName 반납 완료")
    }

    @Tag(name = "MANAGER MODE")
    @PostMapping("/manager/purchase-book/set-total-balance")
    fun setBalance(@RequestParam deposit: Int) = purchaseBookService.depositIntoAccount(deposit)

    @Tag(name = "MANAGER MODE")
    @PostMapping("/manager/purchase-book/purchase")
    fun purchase(@RequestBody bookInfo: BookInfoModel): BookInfoModel = purchaseBookService.purchaseBook(bookInfo)

    @Tag(name = "MANAGER MODE")
    @GetMapping("/manager/purchase-book/history")
    fun findHistory(): List<PurchaseBookHistoryModel> = purchaseBookService.findPurchaseHistory()


    @Tag(name = "MEMBER MODE", description = "Only available for members")
    @PostMapping("/member/get-my-status-update-records")
    fun findStatusUpdateRecordById(@RequestParam memberLoginId: String) : List<StatusUpdateRecordsModel> = commonService.findStatusUpdateRecordsById(memberLoginId)

    @Tag(name = "MEMBER MODE")
    @PostMapping("/member/search-book/")
    @Operation(description = "You can write book name and author name together or either one of them.")
    fun searchBook(@RequestBody searchInfo: SearchInfoModel) : Any {

        if ((searchInfo.bookName != null) && (searchInfo.author == "")) {
            if (!commonService.isValidBook(searchInfo.bookName)) return ResponseModel(false, "해당 제목의 도서없음")
            return searchBookService.byBookName(searchInfo.bookName)
        }
        else if ((searchInfo.bookName == "") && (searchInfo.author != null)) {
            if (!commonService.isValidAuthor(searchInfo.author)) return ResponseModel(false, "해당 저자의 도서없음")
            return searchBookService.byAuthor(searchInfo.author)
        }
        else if ((searchInfo.bookName != null) && (searchInfo.author != null))


        return ResponseModel(false, "도서명 혹은 저자명 중 하나는 입력해야 함.")

    }



}


