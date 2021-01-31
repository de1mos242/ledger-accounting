package net.de1mos.ledger.accounting

import net.de1mos.ledger.accounting.services.AccountingProcessor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding

@SpringBootApplication
@EnableBinding(AccountingProcessor::class)
class AccountingApplication

fun main(args: Array<String>) {
    runApplication<AccountingApplication>(*args)
}
