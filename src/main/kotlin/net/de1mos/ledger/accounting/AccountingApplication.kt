package net.de1mos.ledger.accounting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AccountingApplication

fun main(args: Array<String>) {
    runApplication<AccountingApplication>(*args)
}
