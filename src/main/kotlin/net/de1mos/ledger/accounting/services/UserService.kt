package net.de1mos.ledger.accounting.services

import net.de1mos.ledger.accounting.Account2UserRepository
import net.de1mos.ledger.accounting.AccountRepository
import net.de1mos.ledger.accounting.UserRepository
import net.de1mos.ledger.accounting.models.Account
import net.de1mos.ledger.accounting.models.Account2User
import net.de1mos.ledger.accounting.models.TokenInfo
import net.de1mos.ledger.accounting.models.User
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
    private val account2UserRepository: Account2UserRepository
) {

    fun getOrRegisterUser(exchange: ServerWebExchange): Mono<User> {
        return getTokenInfo(exchange).flatMap { token ->
            userRepository.findByExternalUUID(UUID.fromString(token.id)).switchIfEmpty(registerUser(token))
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
            account2UserRepository.save(Account2User(accountId = it.t2.id!!, userId = it.t1.id!!)).thenReturn(it.t1)
        }

    }
}