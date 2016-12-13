package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAccountType
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Account Type model
 */
data class AccountType(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String,
        val symbol: String
) : Dto {

    override fun getDbClass(): Class<out RealmAccountType> {
        return RealmAccountType::class.java
    }

    override fun isValid(): Boolean {
        return name.isNotEmpty()
    }

    override fun toDb(): RealmAccountType {
        return convertToDb(AccountType::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    val CURRENT = "1"
    val SAVINGS = "2"
    val CREDIT_CARD = "3"
    val PETTY_CASH = "4"
    val CASH = "5"
    val LOAN = "6"
    val OTHER = "7"
}