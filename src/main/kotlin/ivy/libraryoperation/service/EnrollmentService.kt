package ivy.libraryoperation.service

import ivy.libraryoperation.controller.ManagerController
import ivy.libraryoperation.model.EnrollInfoModel
import ivy.libraryoperation.model.ResponseModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class EnrollmentService(val db: JdbcTemplate) {

    fun enrollManager(enrollInfo: EnrollInfoModel): ResponseModel {
        db.update("insert into managers (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")

        return ResponseModel(true, enrollInfo.toString())
    }


    fun enrollMember(enrollInfo: EnrollInfoModel): ResponseModel  {
        db.update("insert into members (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")

        return ResponseModel(true, enrollInfo.toString())
    }


    fun findPeopleList(type: String): List<EnrollInfoModel> {
        var result = emptyList<EnrollInfoModel>()

        when (type) {
            "manager" -> {
                result = db.query("select * from managers")
                { response, _ -> EnrollInfoModel(response.getString("loginId"), response.getString("password")) }
            }

            "member" -> {
                result = db.query("select * from members")
                { response, _ -> EnrollInfoModel(response.getString("loginId"), response.getString("password")) }
            }
        }

        return result
    }

}