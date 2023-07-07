package ivy.libraryoperation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class LibraryOperationApplication

fun main(args: Array<String>) {
    runApplication<LibraryOperationApplication>(*args)
}

