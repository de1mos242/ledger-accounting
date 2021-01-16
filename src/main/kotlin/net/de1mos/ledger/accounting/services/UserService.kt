package net.de1mos.ledger.accounting.services

import net.de1mos.ledger.accounting.models.TokenInfo
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Principal

@Service
class UserService {

    fun getUserInfo(exchange: ServerWebExchange): Mono<TokenInfo> {
        return exchange.getPrincipal<Principal>()
            .switchIfEmpty(Mono.error(IllegalStateException("Principal is empty")))
            .flatMap {
                if (it is JwtAuthenticationToken) {
                    val claims = (it.credentials as Jwt).claims
                    Mono.just(TokenInfo(
                        claims.get("sub") as String,
                        claims.get("given_name") as String,
                        claims.get("family_name") as String,
                        claims.get("email") as String))
                } else {
                    Mono.empty()
                }
            }
    }
}