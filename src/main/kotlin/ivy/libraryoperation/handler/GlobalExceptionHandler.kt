package ivy.libraryoperation.handler

import ivy.libraryoperation.model.ResponseModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.dao.DuplicateKeyException
import java.sql.SQLSyntaxErrorException


@RestControllerAdvice
class GlobalExceptionHandler {

    // controller 게층에서 일어나는 에러 처리
    @ExceptionHandler(SQLSyntaxErrorException::class)
    fun handleSqlException(e: SQLSyntaxErrorException): ResponseEntity<ResponseModel> {
        val response = ResponseModel(
            result = e.message ?: "sql 구문 확인",
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR) // 체크
    }


    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(e: DuplicateKeyException): ResponseEntity<ResponseModel> {
        val response = ResponseModel(
            result = e.message ?: "중복된 loginId",
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR) // 체크
    }


    @ExceptionHandler(IndexOutOfBoundsException::class)
    fun handleIndexOutOfBoundsException(e: IndexOutOfBoundsException): ResponseEntity<ResponseModel> {
        val response = ResponseModel(
            result = e.message ?: "관련 table에 첫 행 setting 필요"
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST) // 체크
    }


    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ResponseModel> {
        val response = ResponseModel(
            result = e.message
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ResponseModel> {
        val response = ResponseModel(
            result = e.message ?: "서버에게 연락하세요!"
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST) // 체크
    }


}