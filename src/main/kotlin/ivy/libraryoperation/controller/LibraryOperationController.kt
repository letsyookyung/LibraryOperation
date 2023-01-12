package ivy.libraryoperation.controller

import ivy.libraryoperation.model.BookInfoModel
import ivy.libraryoperation.model.PurchaseBookHistoryModel
import ivy.libraryoperation.service.CommonService
import ivy.libraryoperation.service.PurchaseBookService
import ivy.libraryoperation.service.UpdateBookListService
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import kotlin.math.log

@RestController
//@RequestMapping("/mode")
class LibraryOperationController (
    private val db: JdbcTemplate,
    private val purchaseBookService: PurchaseBookService,
    private val commonService: CommonService,
    private val updateBookListService: UpdateBookListService,
) {

    class ResponseModel(val isSuccess: Boolean? = false, result: Any)

    @PostMapping("/enroll")
    fun enroll(@RequestParam type: String, loginId: String, password: String) : ResponseModel {
        when (type) {
            "manager" -> db.update("insert into managers (loginId, password) values (${loginId}, ${password})")
            "member" -> db.update("insert into members (loginId, password) values (${loginId}, ${password})")
        }

        return ResponseModel(true, "")
    }

    // manager mode
    @GetMapping("/manager/get-book-list")  //findOnlyAvailable : 대여 가능한 책들만 보기
    fun findBookList(@RequestParam findOnlyAvailable: Boolean) : List<BookInfoModel> = commonService.findBookList(findOnlyAvailable)
//
    @PostMapping("/manager/update-book-list/checkOut")
    fun checkOut(@RequestParam memberLoginId: String, @RequestBody bookInfo: BookInfoModel) : ResponseModel {
        if (!commonService.isValidMember(memberLoginId)) return ResponseModel(false, "memberLoginId 확인")

        if (!commonService.isValidBook(bookInfo.name)) return ResponseModel(false, "도서 제목 확인")

        updateBookListService.checkOut(memberLoginId, bookInfo)

        return ResponseModel(true, bookInfo)
    }

//    @PostMapping("/manager/update-book-list/return")
//    fun return()

    @PostMapping("/manager/purchase-book/set-total-balance")
    fun setBalance(@RequestParam deposit: Int) = purchaseBookService.depositIntoAccount(deposit)

    @PostMapping("/manager/purchase-book/purchase") //http://localhost:8888/manager/purchase-book
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


