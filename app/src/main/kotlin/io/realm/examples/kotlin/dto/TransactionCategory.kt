package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTransactionCategory

/**
 * Common Transaction Category model.
 *
 * BASIC ENTITY (no dependencies)
 * NOTE: In accounting V3, this entity is "ledger_accounts".
 */
data class TransactionCategory(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val label: String = "",
        val moneyIn: Boolean = false,
        val nominalCode: Int = 0,
        val taxCodeId: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmTransactionCategory> {
        return RealmTransactionCategory::class.java
    }

    override fun checkValid(): Dto {
        if (label.isBlank()) {
            throw InvalidFieldException("TransactionCategory label can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmTransactionCategory {
        return convertToDb(TransactionCategory::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return label
    }

    // Convenient factory methods
    companion object {

        enum class V3(val label: String, val moneyIn: Boolean, val nominalCode: Int, val taxCodeId: String) {
            OTHER_RECEIPT("Money in", true, 0, "0"),
            OTHER_PAYMENT("Money out", false, 0, "0"),
            BANK_TRANSFER("Bank transfer", false, 0, "0"),
            DEPOSIT("Deposit", false, 0, "0")
        }

        fun create(type: V3): TransactionCategory {
            return TransactionCategory(type.name, SyncStatus.getDefault(), type.label, type.moneyIn, type.nominalCode, type.taxCodeId)
        }

    }

}