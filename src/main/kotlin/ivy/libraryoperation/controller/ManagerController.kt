package ivy.libraryoperation.controller

import io.swagger.v3.oas.annotations.Operation
import ivy.libraryoperation.model.*
import ivy.libraryoperation.service.*

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import kotlin.math.log


@RestController
@RequestMapping("/manager")
class ManagerController (
    private val db: JdbcTemplate,
    private val loginService: LoginService,
    private val enrollmentService: EnrollmentService,
    private val purchaseBookService: PurchaseBookService,
    private val dataValidationService: DataValidationService,
    private val updateBookListService: UpdateBookListService,
    private val searchBookService: SearchBookService,
) {

    companion object {
        private var availableToUseProgramFlag: Boolean = false
        private val LOGIN_ID_ERROR_MESSAGE = "Manager 목록에 존재하지 않는 ID 입니다."
        private val LOGIN_PWD_ERROR_MESSAGE = "ID는 존재하지만, 비밀번호가 틀립니다."
        private val LOGIN_PLEASE = "Manager 모드로 로그인 후 이용해주세요."
        private val DUPLICATE_ID_ERROR_MESSAGE = "ID 중복."
        private val NO_ID_ERROR_MESSAGE = "존재하지 않는 ID 입니다."
        private val BOOK_NAME_ERROR_MESSAGE = "해당 도서 없음"
        private val UNAVAILBLE_TO_CHECKOUT_ERROR_MESSAGE = "해당 도서의 대여 가능 수량이 0 입니다."
        private val UNAVAILABLE_TO_RETURN_ERROR_MESSAGE = "해당 도서의 반납 대기 수량이 0 입니다."
        private val NO_NECESSARY_TO_RETURN_ERROR_MESSAGE = "해당 ID는 도서를 대여하고 있지 않습니다."
        private val EXCEED_TOTAL_BALANCE_ERROR_MESSAGE = "도서 가격이 총 예산을 넘습니다."
        private val BOOK_PRICE_ERROR_MESSAGE = "도서 가격은 1000원 이상이어야 함"
    }

    @PostMapping("/login")
    fun login(@RequestBody loginInfo: LoginModel): ResponseEntity<ResponseModel> {
        if (!loginService.isManager(loginInfo)) return ResponseEntity(ResponseModel(LOGIN_ID_ERROR_MESSAGE), HttpStatus.FORBIDDEN)

        if (!loginService.isCorrectPassword((loginInfo))) return ResponseEntity(ResponseModel(LOGIN_PWD_ERROR_MESSAGE), HttpStatus.FORBIDDEN)

        availableToUseProgramFlag = true

        return ResponseEntity(HttpStatus.OK)
    }


    @DeleteMapping("/{table}")
    @Operation(description = " Existing db tables:\n - managers \n - members \n - purchaseHistory \n - statusUpdateRecords \n - totalBalance \n - bookList \n")
    fun deleteTables(@PathVariable table: String): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        db.update("delete from $table")

        return ResponseEntity(HttpStatus.OK)
    }


    @PostMapping("/enroll")
    @Operation(description = " Write 'manager' or 'member' in *type* field. ")
    fun enroll(@RequestBody enrollInfo: EnrollInfoModel): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        when (enrollInfo.type) {
            "manager" -> {
                if (!dataValidationService.isValidManager(enrollInfo.loginId)) {
                    enrollmentService.enrollManager(enrollInfo)
                } else {
                    return ResponseEntity(ResponseModel(DUPLICATE_ID_ERROR_MESSAGE), HttpStatus.FORBIDDEN) //체크
                }
            }

            "member" -> {
                if (!dataValidationService.isValidMember(enrollInfo.loginId)) {
                    enrollmentService.enrollMember(enrollInfo)
                } else {
                    return ResponseEntity(ResponseModel(DUPLICATE_ID_ERROR_MESSAGE), HttpStatus.FORBIDDEN) //체크
                }
            }
        }
        return ResponseEntity(HttpStatus.OK)
    }


    @GetMapping("/people-list")
    @Operation(description = " Write 'manager' or 'member' in *type* field. ")
    fun getPeopleList(@RequestParam type: String): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        return ResponseEntity(ResponseModel(enrollmentService.findPeopleList(type)), HttpStatus.OK)
    }


    @GetMapping("/book-list")
    fun findBookList(@RequestParam findOnlyAvailable: Boolean): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        return ResponseEntity(ResponseModel(searchBookService.findBookList(findOnlyAvailable)), HttpStatus.OK)
    }

    @GetMapping("/status-update-records")
    fun findStatusUpdateRecords(): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        return ResponseEntity(ResponseModel(updateBookListService.findStatusUpdateRecords()), HttpStatus.OK)
    }


    @PostMapping("/update-book-list/checkOut")
    fun checkOutBook(@RequestBody checkOutInfoModel: CheckOutReturnInfoModel): ResponseEntity<ResponseModel> {
        val mode = "checkOut"

        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        if (!dataValidationService.isValidMember(checkOutInfoModel.memberLoginId)) return ResponseEntity(ResponseModel(NO_ID_ERROR_MESSAGE), HttpStatus.OK) //체크

        if (!dataValidationService.isValidBook(checkOutInfoModel.bookName)) return ResponseEntity(ResponseModel(BOOK_NAME_ERROR_MESSAGE), HttpStatus.OK) //체크

        if (!dataValidationService.isAvailableToCheckOut(mode, checkOutInfoModel.bookName)) return ResponseEntity(ResponseModel(UNAVAILBLE_TO_CHECKOUT_ERROR_MESSAGE), HttpStatus.OK) //체크

        updateBookListService.checkOutBook(checkOutInfoModel.memberLoginId, checkOutInfoModel.bookName)

        return ResponseEntity(HttpStatus.OK)

    }


    @PostMapping("/update-book-list/return")
    fun returnBook(@RequestBody returnInfoModel: CheckOutReturnInfoModel): ResponseEntity<ResponseModel> {
        val mode = "return"

        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        if (!dataValidationService.isValidMember(returnInfoModel.memberLoginId)) return ResponseEntity(ResponseModel(NO_ID_ERROR_MESSAGE), HttpStatus.OK) //체크

        if (!dataValidationService.isValidBook(returnInfoModel.bookName)) return ResponseEntity(ResponseModel(BOOK_NAME_ERROR_MESSAGE), HttpStatus.OK) //체크

        if (!dataValidationService.isAvailableToCheckOut(mode, returnInfoModel.bookName)) return ResponseEntity(ResponseModel(UNAVAILABLE_TO_RETURN_ERROR_MESSAGE), HttpStatus.OK) //체


        val bookId = dataValidationService.findMatchingNumberedID(returnInfoModel.memberLoginId, returnInfoModel.bookName)[0]
        val memberId = dataValidationService.findMatchingNumberedID(returnInfoModel.memberLoginId, returnInfoModel.bookName)[1]

        if (!updateBookListService.isCheckedOutById(memberId, bookId)) return ResponseEntity(ResponseModel(NO_NECESSARY_TO_RETURN_ERROR_MESSAGE), HttpStatus.OK) //체크

        updateBookListService.returnBook(memberId, returnInfoModel.memberLoginId, bookId, returnInfoModel.bookName)

        return ResponseEntity(HttpStatus.OK)
    }


    @PostMapping("/purchase-book/set-total-balance")
    fun setBalance(@RequestBody deposit: Int): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        purchaseBookService.depositIntoAccount(deposit)

        return ResponseEntity(HttpStatus.OK)
    }


    @PostMapping("/purchase-book/purchase")
    fun purchase(@RequestBody bookInfo: BookInfoModel): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        val priorTotalBalance: Int = db.query("select totalBalance from totalBalance order by date desc limit 1")
        { response, _ -> response.getInt("totalBalance") }[0]

        if ((priorTotalBalance - bookInfo.price) <= 0) {
            return ResponseEntity(ResponseModel(EXCEED_TOTAL_BALANCE_ERROR_MESSAGE), HttpStatus.OK) //체크
        } else if (bookInfo.price <= 1000) {
            return ResponseEntity(ResponseModel(BOOK_PRICE_ERROR_MESSAGE), HttpStatus.OK) //체크
        }

        purchaseBookService.purchaseBook(bookInfo, priorTotalBalance)

        return ResponseEntity(HttpStatus.OK)

    }


    @GetMapping("/purchase-book/history")
    fun findHistory(): ResponseEntity<ResponseModel> {
        if (!availableToUseProgramFlag) return ResponseEntity(ResponseModel(LOGIN_PLEASE), HttpStatus.OK)

        val response = ResponseModel(purchaseBookService.findPurchaseHistory())

        return ResponseEntity(response, HttpStatus.OK)
    }

}


