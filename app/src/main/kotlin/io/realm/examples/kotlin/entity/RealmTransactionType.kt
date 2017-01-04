package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.TransactionType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmTransactionType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var ordinal: Int = 0
) : RealmDbModel {

    override fun toDto(): TransactionType {
        return convertToDto(RealmTransactionType::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (ordinal < TransactionType.INCOME || ordinal > TransactionType.BANK_DEPOSIT) {
            throw IllegalArgumentException("RealmTransactionType ordinal out of range!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out TransactionType> {
        return TransactionType::class.java
    }


}
