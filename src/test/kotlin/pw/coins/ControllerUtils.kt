package pw.coins

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

fun MockMvc.jsonPost(
    urlTemplate: String,
    vararg vars: Any?,
    dsl: MockHttpServletRequestDsl.() -> Unit
): ResultActionsDsl {
    return post(urlTemplate, vars) {
        contentType = MediaType.APPLICATION_JSON
        accept = MediaType.APPLICATION_JSON
        dsl(this)
    }
}

fun MockMvc.jsonGet(
    urlTemplate: String,
    vararg vars: Any?,
    dsl: MockHttpServletRequestDsl.() -> Unit
): ResultActionsDsl {
    return get(urlTemplate, vars) {
        accept = MediaType.APPLICATION_JSON
        dsl(this)
    }
}

fun ResultActionsDsl.andExpectOkJson(dsl: MockMvcResultMatchersDsl.() -> Unit): ResultActionsDsl {
    return andExpect {
        status { isOk() }
        content { contentType(MediaType.APPLICATION_JSON) }
        dsl(this)
    }
}
