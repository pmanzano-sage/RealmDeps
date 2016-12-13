package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAccount
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Bank Account model
 */
data class Account(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val displayName: String = "",
        val number: String = "",
        var accountType: AccountType? = null,
        val nominalCode: Int = 0,
        var balance: Amount
) : Dto {

    var editable: Boolean = true

    override fun getDbClass(): Class<out RealmAccount> {
        return RealmAccount::class.java
    }

    override fun isValid(): Boolean {
        return displayName.isNotEmpty()
    }

    override fun toDb(): RealmAccount {
        return convertToDb(Account::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return displayName
    }

}