package pw.coins.transaction

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pw.coins.transaction.dtos.Transaction

@RestController
@RequestMapping("/transaction")
@Tag(name = "Transaction")
class TransactionCo(val transactionSe: TransactionSe) {

    @GetMapping("test/{args}")
    fun testTransaction(@PathVariable args: String) {
        val (from, to) = args.split("A")
        transactionSe.executeTransaction(
            Transaction(from.toLong(), to.toLong(), 10)
        )
    }
}