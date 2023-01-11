package ivy.libraryoperation.controller

import ivy.libraryoperation.model.BookInfo
import ivy.libraryoperation.model.PurchaseBookHistoryInfo
import ivy.libraryoperation.service.PurchaseBookService
import org.springframework.web.bind.annotation.*

@RestController
//@RequestMapping("/mode")
class LibraryOperationController (
    private val purchaseBookService: PurchaseBookService

) {

    @GetMapping("/") // http://localhost:8888?name=John
    fun index(@RequestParam("name") name: String) = "Hello, $name!"

    // manager mode
//    @GetMapping("/manager/check-book-list")
//
//    @PostMapping("/manager/update-book-list")

//    @PostMapping("/manager/purchase-book") //http://localhost:8888/manager/purchase-book
    @PostMapping("/manager/purchase-book") //http://localhost:8888/manager/purchase-book
    fun purchaseBook(@RequestBody bookInfo: BookInfo) {
//        if ((purchaseBookHistoryInfo.totalBalance - bookInfo.price) <= 0) {
//            throw RuntimeException("도서 가격 비쌈")
//        }
        purchaseBookService.addInPurchaseHistory(bookInfo)
        purchaseBookService.addInBookList(bookInfo)
    }

    @GetMapping("/manager/purchase-book/history")
    fun checkPurchaseBookHistory() {
        purchaseBookService.findPurchaseHistory()
    }


    // member mode
//    @PostMapping("/member/get-my-check-out-status")
//
//    @PostMapping("/member/search-book")
//
//    @PostMapping("/member/borrow-book")
//
//    @PostMapping("/member/return-book")


}


