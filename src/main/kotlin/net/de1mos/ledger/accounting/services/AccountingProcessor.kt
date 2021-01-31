package net.de1mos.ledger.accounting.services

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import java.util.*

data class User2AccountMessage(val userUUID: UUID, val accountUUID: UUID)

const val PARTITION_KEY_NAME = "partitionKey"

interface AccountingProcessor {

    @Output("a2u_event")
    fun a2uEventOutput(): MessageChannel
}