package net.de1mos.ledger.accounting.services

import net.de1mos.ledger.accounting.Account2UserEventRepository
import net.de1mos.ledger.accounting.Account2UserRepository
import net.de1mos.ledger.accounting.AccountRepository
import net.de1mos.ledger.accounting.UserRepository
import net.de1mos.ledger.accounting.models.Account
import net.de1mos.ledger.accounting.models.Account2User
import net.de1mos.ledger.accounting.models.Account2UserEvent
import net.de1mos.ledger.accounting.models.TokenInfo
import net.de1mos.ledger.accounting.models.User
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val account2UserRepository: Account2UserRepository,
    private val account2UserEventRepository: Account2UserEventRepository,
    private val accountingProcessor: AccountingProcessor
) {

    fun getOrRegisterUser(exchange: ServerWebExchange): Mono<User> {
        return getTokenInfo(exchange).flatMap { token ->
            userRepository.findByExternalUUID(UUID.fromString(token.id)).switchIfEmpty(registerUser(token))
        }.doOnNext {
            accountingProcessor.debugOutput().send(
                MessageBuilder.withPayload(
                    User2AccountMessage(it.externalUUID, UUID.randomUUID())
                ).setHeader(PARTITION_KEY_NAME, it.externalUUID).build()
            )
        }
    }

    private fun getTokenInfo(exchange: ServerWebExchange): Mono<TokenInfo> {
        return exchange.getPrincipal<Principal>()
            .switchIfEmpty(Mono.error(IllegalStateException("Principal is empty")))
            .flatMap {
                if (it is JwtAuthenticationToken) {
                    val claims = (it.credentials as Jwt).claims
                    Mono.just(
                        TokenInfo(
                            claims["sub"] as String,
                            claims["given_name"] as String,
                            claims["family_name"] as String,
                            claims["email"] as String
                        )
                    )
                } else {
                    Mono.error(IllegalStateException("Principal is not jwt"))
                }
            }
    }

    private fun registerUser(tokenInfo: TokenInfo): Mono<User> {
        val user = User(tokenInfo.firstName, tokenInfo.lastName, tokenInfo.email, UUID.fromString(tokenInfo.id))
        val account = Account("default", UUID.randomUUID())
        val saveMain = userRepository.save(user).zipWith(accountRepository.save(account))
        return saveMain.flatMap {
            val (savedUser, savedAccount) = it.t1 to it.t2
            account2UserRepository.save(Account2User(accountId = savedAccount.id!!, userId = savedUser.id!!))
//                .doOnNext {
//                    accountingProcessor.a2uEventOutput().send(
//                        MessageBuilder.withPayload(
//                            User2AccountMessage(savedUser.externalUUID, savedAccount.externalUUID)
//                        ).setHeader(PARTITION_KEY_NAME, savedAccount.externalUUID).build()
//                    )
//                }
                .flatMap {
                    val account2UserEvent =
                        Account2UserEvent(accountUUID = savedAccount.externalUUID, userUUID = savedUser.externalUUID)
                    account2UserEventRepository.save(account2UserEvent)
                }
                .thenReturn(it.t1)
        }

    }
}