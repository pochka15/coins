package pw.coins.root

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/")
class RootController {
    @GetMapping
    fun redirectSwagger(response: HttpServletResponse) {
        response.sendRedirect("/swagger-ui/index.html#/")
    }

    @GetMapping
    @RequestMapping("tmp")
    fun tmp(): String {
        return "Hello world"
    }
}