package net.de1mos.ledger.accounting.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("users")
data class User(
    @Column("first_name") var firstName: String,
    @Column("last_name") var lastName: String,
    @Column("email") var email: String,
    @Column("external_uuid") var externalUUID: UUID,
    @Id @Column("id") var id: Long? = null
)

@Table("accounts")
data class Account(
    @Column("name") var name: String,
    @Column("external_uuid") var externalUUID: UUID,
    @Id @Column("id") var id: Long? = null
)

@Table("a2u")
data class Account2User(
    @Column("account_id") var accountId: Long,
    @Column("user_id") var userId: Long,
    @Id @Column("id") var id: Long? = null
)