package ivy.libraryoperation.controller

import io.swagger.v3.oas.annotations.Operation
import ivy.libraryoperation.model.*
import ivy.libraryoperation.service.*

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.dao.DuplicateKeyException
import java.sql.SQLSyntaxErrorException


@RestController
@RequestMapping("/manager")
class ManagerController (
    private val db: JdbcTemplate,
    private val enrollmentService: EnrollmentService,
    private val purchaseBookService: PurchaseBookService,
    private val dataValidationService: DataValidationService,
    private val updateBookListService: UpdateBookListService,
    private val searchBookService: SearchBookService,
) {

    @DeleteMapping("/{table}")
    @Operation(description = " Existing db tables:\n - managers \n - members \n - purchaseHistory \n - statusUpdateRecords \n - totalBalance \n - bookList \n" )
    fun deleteTables(@PathVariable table: String): ResponseModel {
        db.update("delete from $table")
        return ResponseModel(true, "$table 내용 모두 삭제")
    }


    @PostMapping("/enroll")
    @Operation(description = " Write 'manager' or 'member' in *type* field. ")
    fun enroll(@RequestParam type: String, @RequestBody enrollInfo: EnrollInfoModel): ResponseModel {
        try {
            when (type) {
                "manager" -> run {
                    return if (dataValidationService.isValidManager(enrollInfo.loginId)) {
                        ResponseModel(
                            false,
                            "아이디중복"
                        )
                    } else {
                        enrollmentService.enrollManager(enrollInfo)
                    }
                }

                "member" -> run {
                    return if (dataValidationService.isValidMember(enrollInfo.loginId)) {
                        ResponseModel(
                            false,
                            "아이디중복"
                        )
                    } else {
                        enrollmentService.enrollMember(enrollInfo)
                    }
                }
            }
        }catch (e: SQLSyntaxErrorException) {
            println("error: ${e.message}")
            throw SQLSyntaxErrorException()
        } catch (e: DuplicateKeyException) {
            println("error: $e")
            return ResponseModel(false, "중복된 numbered id")
        } catch (e: Exception) {
            println("error: ${e.message}")
            throw Exception()
        }

        return ResponseModel(true, "아이디 저장함")
    }


    @GetMapping("/people-list")
    @Operation(description = " Write 'manager' or 'member' in *type* field. ")
    fun getPeopleList(@RequestParam type: String): List<EnrollInfoModel> = enrollmentService.findPeopleList(type)

    
    @GetMapping("/book-list")
    fun findBookList(@RequestParam findOnlyAvailable: Boolean): List<BookInfoModel> = searchBookService.findBookList(findOnlyAvailable)

    
    @GetMapping("/status-update-records")
    fun findStatusUpdateRecords(): List<StatusUpdateRecordsModel> = updateBookListService.findStatusUpdateRecords()


    @PostMapping("/update-book-list/checkOut")
    fun checkOutBook(@RequestBody memberLoginId: String, bookName: String) : ResponseModel {
        val mode = "checkOut"

        if (!dataValidationService.isValidMember(memberLoginId)) return ResponseModel(false, "id없음")

        if (!dataValidationService.isValidBook(bookName)) return ResponseModel(false, "도서없음")

        if (!dataValidationService.isAvailableToCheckOut(mode, bookName)) return ResponseModel(false, "대여중임")

        updateBookListService.checkOutBook(memberLoginId, bookName)

        return ResponseModel(true, "$memberLoginId 님, $bookName 대여 완료")
    }


    @PostMapping("/update-book-list/return")
    fun returnBook(@RequestBody memberLoginId: String, bookName: String) : ResponseModel {
        val mode = "return"

        if (!dataValidationService.isValidMember(memberLoginId)) return ResponseModel(false, "id없음")

        if (!dataValidationService.isValidBook(bookName)) return ResponseModel(false, "도서없음")

        if (!dataValidationService.isAvailableToCheckOut(mode, bookName)) return ResponseModel(false, "대여중이 아님")

        val bookId = dataValidationService.findMatchingNumberedID(memberLoginId, bookName)[0]
        val memberId = dataValidationService.findMatchingNumberedID(memberLoginId, bookName)[1]

        if (!updateBookListService.isCheckedOutById(memberId, bookId)) return ResponseModel(false, "$memberLoginId 님, 해당 도서를 대여하고 있지 않음")

        updateBookListService.returnBook(memberId, memberLoginId, bookId, bookName)

        return ResponseModel(true, "$memberLoginId 님, $bookName 반납 완료")
    }


    @PostMapping("/purchase-book/set-total-balance")
    fun setBalance(@RequestBody deposit: Int) = purchaseBookService.depositIntoAccount(deposit)


    @PostMapping("/purchase-book/purchase")
    fun purchase(@RequestBody bookInfo: BookInfoModel): ResponseModel {
        var priorTotalBalance: Int

        try {
            priorTotalBalance = db.query("select totalBalance from totalBalance order by date desc limit 1")
            {response, _ -> response.getInt("totalBalance")}[0]
        } catch (e: IndexOutOfBoundsException) {
            println("error: set total balance 필요 ${e.message} ")
            throw IndexOutOfBoundsException()
        }

        if ((priorTotalBalance- bookInfo.price) <= 0) {
            return ResponseModel(false, "도서 가격이 총 예산을 넘음")
        } else if (bookInfo.price <= 1000 ) {
            return ResponseModel(false, "도서 가격은 1000원 이상이어야 함")
        }

        return purchaseBookService.purchaseBook(bookInfo, priorTotalBalance)

    }


    @GetMapping("/purchase-book/history")
    fun findHistory(): List<PurchaseBookHistoryModel> = purchaseBookService.findPurchaseHistory()


}


