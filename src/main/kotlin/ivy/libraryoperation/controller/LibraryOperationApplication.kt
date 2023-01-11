package ivy.libraryoperation.controller

import ivy.libraryoperation.model.BookInfo
import ivy.libraryoperation.model.PurchaseHistoryModel
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class LibraryOperationApplication

fun main(args: Array<String>) {
    runApplication<LibraryOperationApplication>(*args)
}

@RestController
@RequestMapping("/mode")
class LibraryOperationController(
    val pur,
    val,
    val,
) {

    // manager mode
    @GetMapping("/manager/check-book-list")

    @PostMapping("/manager/update-book-list")

    @PostMapping("/manager/purchase-book")
    fun purchaseBook(@RequestBody bookInfo: BookInfo, purchaseHistoryModel: PurchaseHistoryModel) {
        if ((purchaseHistoryModel.totalBalance - bookInfo.price) <=0) {
            throw RuntimeException("도서 가격 비쌈")
        }



        //가격 체크

    }

    @GetMapping("/manager/purchase-book/history")


    // member mode
    @PostMapping("/member/get-my-check-out-status")

    @PostMapping("/member/search-book")

    @PostMapping("/member/borrow-book")

    @PostMapping("/member/return-book")









}
