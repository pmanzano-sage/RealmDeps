package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTransactionCategory

/**
 * Common Transaction Category model.
 *
 * BASIC ENTITY (no dependencies)
 */
data class TransactionCategory(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val label: String,
        val moneyIn: Boolean,
        val nominalCode: Int,
        val taxCodeId: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmTransactionCategory> {
        return RealmTransactionCategory::class.java
    }

    override fun checkValid(): Dto {
        if (label.isBlank()) {
            throw IllegalArgumentException("TransactionCategory label can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmTransactionCategory {
        return convertToDb(TransactionCategory::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return label
    }
}