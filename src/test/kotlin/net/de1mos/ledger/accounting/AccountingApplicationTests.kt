package net.de1mos.ledger.accounting

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureWebTestClient
class AccountingApplicationTests {

    protected val userUrl = "/api/recorder/user"

    @Autowired
    lateinit var context: ApplicationContext

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Test
    fun contextLoads() {
    }

}
