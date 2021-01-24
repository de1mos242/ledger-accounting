package net.de1mos.ledger.accounting.controllers

import net.de1mos.ledger.accounting.api.UsersApi
import net.de1mos.ledger.accounting.api.models.UserInfo
import net.de1mos.ledger.accounting.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
class UserController(private val userService: UserService) : UsersApi {
    override fun getUserInfo(exchange: ServerWebExchange): Mono<ResponseEntity<UserInfo>> {
        return userService.getOrRegisterUser(exchange).map {
            ResponseEntity.ok(
                UserInfo()
                    .email(it.email).firstName(it.firstName).lastName(it.lastName).id(it.externalUUID.toString())
            )
        }
    }
}