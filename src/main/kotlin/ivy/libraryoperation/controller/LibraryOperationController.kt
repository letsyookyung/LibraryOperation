package ivy.libraryoperation.controller

import ivy.libraryoperation.model.BookInfoModel
import ivy.libraryoperation.model.EnrollInfoModel
import ivy.libraryoperation.model.PurchaseBookHistoryModel
import ivy.libraryoperation.service.CommonService
import ivy.libraryoperation.service.PurchaseBookService
import ivy.libraryoperation.service.UpdateBookListService
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.dao.DuplicateKeyException
import java.sql.SQLSyntaxErrorException
import kotlin.math.log

@RestController
//@RequestMapping("/mode")
class LibraryOperationController (
    private val db: JdbcTemplate,
    private val purchaseBookService: PurchaseBookService,
    private val commonService: CommonService,
    private val updateBookListService: UpdateBookListService,
) {

    data class ResponseModel(val isSuccess: Boolean? = false, val result: String?)

    @PostMapping("/enroll")
    fun enroll(@RequestBody enrollInfo: EnrollInfoModel) : ResponseModel {
        // 아이디 중복 체크
        try {
            when (enrollInfo.type) {
                "manager" -> db.update("insert into managers (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")
                "member" -> db.update("insert into members (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")
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

    // manager mode
    @GetMapping("/manager/get-book-list")  //findOnlyAvailable : 대여 가능한 책들만 보기
    fun findBookList(@RequestParam findOnlyAvailable: Boolean) : List<BookInfoModel> = commonService.findBookList(findOnlyAvailable)
//
    @PostMapping("/manager/update-book-list/checkOut")
    fun checkOutBook(@RequestParam memberLoginId: String, bookName: String) : ResponseModel {
        val mode = "checkOut"

        if (!commonService.isValidMember(memberLoginId)) return ResponseModel(false, "id없음")

        if (!commonService.isValidBook(bookName)) return ResponseModel(false, "도서없음")

        if (!commonService.isAvailableToCheckOut(mode, bookName)) return ResponseModel(false, "대여중임")

        updateBookListService.checkOutBook(memberLoginId, bookName)

        return ResponseModel(true, "${memberLoginId} 님,${bookName} 대여 완료")
    }

    @PostMapping("/manager/update-book-list/return")
    fun returnBook(@RequestParam memberLoginId: String, bookName: String) : ResponseModel {
        val mode = "return"

        if (!commonService.isValidMember(memberLoginId)) return ResponseModel(false, "id없음")

        if (!commonService.isValidBook(bookName)) return ResponseModel(false, "도서없음")

        if (!commonService.isAvailableToCheckOut(mode, bookName)) return ResponseModel(false, "대여중이 아님")

        val bookId = commonService.findMatchingNumberedID(memberLoginId, bookName)[0]
        val memberId = commonService.findMatchingNumberedID(memberLoginId, bookName)[1]

        if (!updateBookListService.isCheckedOutById(memberId, bookId)) return ResponseModel(false, "해당 도서를 대여하고 있지 않음")

        updateBookListService.returnBook(bookName, memberId, bookId)

        return ResponseModel(true, "${memberLoginId} 님, ${bookName} 반납 완료")
    }

    @PostMapping("/manager/purchase-book/set-total-balance")
    fun setBalance(@RequestParam deposit: Int) = purchaseBookService.depositIntoAccount(deposit)

    @PostMapping("/manager/purchase-book/purchase")
    fun purchase(@RequestBody bookInfo: BookInfoModel): BookInfoModel = purchaseBookService.purchaseBook(bookInfo)

    @GetMapping("/manager/purchase-book/history")
    fun findHistory(): List<PurchaseBookHistoryModel> = purchaseBookService.findPurchaseHistory()




    // member mode
//    @PostMapping("/member/get-my-check-out-status")
//
//    @PostMapping("/member/search-book")
//
//    @PostMapping("/member/borrow-book")
//
//    @PostMapping("/member/return-book")


}


