package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Transaction
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
open class RealmTransaction(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var title: String = "",
        open var reference: String = "",
        open var date: String = "",
        open var amount: String = "",
        open var currency: String = "",
        open var accountSource: RealmAccount? = null,
        open var accountDest: RealmAccount? = null,
        open var type: Int = 0,
        open var category: RealmTransactionCategory? = null,
        open var attachment: RealmAttachment? = null,
        open var taxRate: RealmTaxRate? = null,
        open var contact: RealmContact? = null,
        open var isReadonly: Boolean = false,
        open var timestamp: String = ""
) : DbModel {

    override fun toDto(): Transaction {
        return convertToDto(RealmTransaction::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Transaction> {
        return Transaction::class.java
    }

}
