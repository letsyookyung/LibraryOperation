import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfig {
//    @Bean
//    fun restAPI(): Docket {
//        return Docket(DocumentationType.SWAGGER_2)
//            .useDefaultResponseMessages(true) // Swagger 에서 제공해주는 기본 응답 코드 (200, 401, 403, 404) 등의 노출 여부
//            .apiInfo(apiInfo()) // Swagger UI 로 노출할 정보
//            .select()
//            .apis(RequestHandlerSelectors.any())
////            .apis(RequestHandlerSelectors.basePackage("ivy.libraryoperation.controller")) // api 스펙이 작성되어 있는 패키지 (controller)
//            .paths(PathSelectors.any()) // apis 에 위치하는 API 중 특정 path 를 선택
//            .build()
//    }
//
//    private fun apiInfo(): ApiInfo {
//        return ApiInfoBuilder()
//            .title("Library Operation REST API")
//            .version("1.0.0")
//            .description("도서관 운영 관리를 하기 위한 swagger api 입니다.")
//            .build()
//    }

    @Bean
    fun api(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build().apiInfo(apiInfo())
    }

    private fun apiInfo(): ApiInfo? {
        val description = "Welcome Log Company"
        return ApiInfoBuilder()
            .title("SWAGGER TEST")
            .description(description)
            .version("1.0")
            .build()
    }
}