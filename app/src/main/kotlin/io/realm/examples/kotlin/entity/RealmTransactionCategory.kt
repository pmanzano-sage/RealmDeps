package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.TransactionCategory
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmTransactionCategory(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var moneyIn: Boolean = false,
        open var nominalCode: Int = 0,
        open var label: String = "",
        open var taxCodeId: String = ""
) : RealmDbModel {

    override fun toDto(): TransactionCategory {
        return convertToDto(RealmTransactionCategory::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (label.isBlank()) {
            throw IllegalArgumentException("RealmTransactionCategory label can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out TransactionCategory> {
        return TransactionCategory::class.java
    }

    companion object {
        val FIELD_NAME = "label"
    }
}
