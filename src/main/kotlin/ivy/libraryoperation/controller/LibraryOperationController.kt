package ivy.libraryoperation.controller

import ivy.libraryoperation.model.BookInfoModel
import ivy.libraryoperation.model.PurchaseBookHistoryModel
import ivy.libraryoperation.service.PurchaseBookService
import org.springframework.web.bind.annotation.*

@RestController
//@RequestMapping("/mode")
class LibraryOperationController (
    private val purchaseBookService: PurchaseBookService
) {

    // manager mode
//    @GetMapping("/manager/check-book-list")
//
//    @PostMapping("/manager/update-book-list")

//    @PostMapping("/manager/purchase-book") //http://localhost:8888/manager/purchase-book

    @PostMapping("/manager/purchase-book/set-total-balance")
    fun set(@RequestParam deposit: Int) = purchaseBookService.depositIntoAccount(deposit)

    @PostMapping("/manager/purchase-book") //http://localhost:8888/manager/purchase-book
    fun purchase(@RequestBody bookInfo: BookInfoModel): BookInfoModel = purchaseBookService.purchaseBook(bookInfo)

    @GetMapping("/manager/purchase-book/history")
    fun getHistory(): List<PurchaseBookHistoryModel> = purchaseBookService.findPurchaseHistory()




    // member mode
//    @PostMapping("/member/get-my-check-out-status")
//
//    @PostMapping("/member/search-book")
//
//    @PostMapping("/member/borrow-book")
//
//    @PostMapping("/member/return-book")


}


