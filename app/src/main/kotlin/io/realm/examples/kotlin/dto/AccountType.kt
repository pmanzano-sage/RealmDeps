package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAccountType

/**
 * Common Account Type model.
 * All parameters must have default values since a no argument constructor is required.
 *
 * BASIC ENTITY (no dependencies)
 */
data class AccountType(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String = "",
        val symbol: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmAccountType> {
        return RealmAccountType::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw InvalidFieldException("AccountType name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmAccountType {
        return convertToDb(AccountType::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    // Convenient factory methods for V2 and V3
    companion object {

        enum class V2(val id: String) {
            CURRENT("1"),
            SAVINGS("2"),
            CREDIT_CARD("3"),
            PETTY_CASH("4"),
            CASH("5"),
            LOAN("6"),
            OTHER("7")
        }

        enum class V3 {
            CHECKING,
            SAVINGS,
            CREDIT_CARD,
            CASH_IN_HAND,
            LOAN,
            OTHER
        }

        fun create(type: V3): AccountType {
            return AccountType(type.name, SyncStatus.getDefault(), type.name.toLowerCase().replace('_', ' '), type.name)
        }

        fun create(type: V2): AccountType {
            return AccountType(type.id, SyncStatus.getDefault(), type.name.toLowerCase().replace('_', ' '), type.name)
        }

    }
}