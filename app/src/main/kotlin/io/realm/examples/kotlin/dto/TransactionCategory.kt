package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTransactionCategory
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Transaction Category model
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

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmTransactionCategory {
        return convertToDb(TransactionCategory::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return label
    }
}