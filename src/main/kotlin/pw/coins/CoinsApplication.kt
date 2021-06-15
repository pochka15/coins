package pw.coins

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoinsApplication

fun main(args: Array<String>) {
    runApplication<CoinsApplication>(*args)
}
