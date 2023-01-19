package ivy.libraryoperation.service

import ivy.libraryoperation.model.LoginModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service


@Service
class LoginService(private val db: JdbcTemplate) {

    fun isManager(loginInfo: LoginModel): Boolean {
        val query = "select * from managers where loginId = '${loginInfo.loginId}'"

        if (db.query(query) { response, _ -> response.getString("managerId") }.size == 0) return false

        return true
    }

    fun isCorrectPassword(loginInfo: LoginModel): Boolean {
        val query = "select * from managers where loginId = '${loginInfo.loginId}' and password = '${loginInfo.password}'"

        if (db.query(query) { response, _ -> response.getString("managerId") }.size == 0) return false

        return true
    }


}