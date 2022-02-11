package pw.coins.root

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/")
class RootCo {
    @GetMapping
    fun redirectSwagger(model: ModelMap): ModelAndView {
        return ModelAndView("redirect:/swagger-ui/index.html#/", model)
    }
}