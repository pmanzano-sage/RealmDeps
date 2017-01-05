package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTransactionType

/**
 * Transaction Type model.
 *
 * BASIC ENTITY (no dependencies)
 *
 * Ordinal values:
 * 0 = INCOME
 * 1 = EXPENSE
 * 2 = BANK_TRANSFER
 * 3 = BANK_DEPOSIT
 */
data class TransactionType(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val ordinal: Int
) : Dto {

    override fun getDbClass(): Class<out RealmTransactionType> {
        return RealmTransactionType::class.java
    }

    override fun checkValid(): Dto {
        if (ordinal < INCOME || ordinal > BANK_DEPOSIT) {
            throw InvalidFieldException("TransactionType ordinal out of range!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmTransactionType {
        return convertToDb(TransactionType::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return ""
    }

    constructor(ordinal: Int) : this("", SyncStatus.getDefault(), ordinal)

    companion object {
        const val INCOME = 0
        const val EXPENSE = 1
        const val BANK_TRANSFER = 2
        const val BANK_DEPOSIT = 3
    }
}