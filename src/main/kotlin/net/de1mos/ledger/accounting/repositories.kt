package net.de1mos.ledger.accounting

import net.de1mos.ledger.accounting.models.Account
import net.de1mos.ledger.accounting.models.Account2User
import net.de1mos.ledger.accounting.models.User
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono
import java.util.*

interface UserRepository: ReactiveSortingRepository<User, Long> {
    fun findByExternalUUID(uuid: UUID): Mono<User>
}

interface AccountRepository: ReactiveSortingRepository<Account, Long> {

}

interface Account2UserRepository: ReactiveSortingRepository<Account2User, Long> {

}