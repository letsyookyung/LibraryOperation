package ivy.libraryoperation.service

import ivy.libraryoperation.model.EnrollInfoModel
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class EnrollmentService(val db: JdbcTemplate) {

    fun enrollManager(enrollInfo: EnrollInfoModel) {
        db.update("insert into managers (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")
    }


    fun enrollMember(enrollInfo: EnrollInfoModel) {
        db.update("insert into members (loginId, password) values ('${enrollInfo.loginId}', '${enrollInfo.password}')")
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