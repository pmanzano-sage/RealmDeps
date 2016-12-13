package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTransaction
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId
import java.text.SimpleDateFormat

/**
 * Common Transaction model
 */
data class Transaction(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var amount: Amount,
        val title: String? = null,
        val reference: String,
        val date: String,
        var accountSource: Account? = null,
        var accountDest: Account? = null,
        val type: Int,
        /** [TransactionType] */
        var category: TransactionCategory? = null,
        var attachment: Attachment? = null,
        var contact: Contact? = null,
        var taxRate: TaxRate? = null,
        val readonly: Boolean = false) : Dto {

    override fun getDbClass(): Class<out RealmTransaction> {
        return RealmTransaction::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmTransaction {
        return convertToDb(Transaction::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return title ?: ""
    }

    var timestamp: String = ""

    companion object {
        @JvmField val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
    }

    fun toAttachmentContextType(): String {
        var contextType = "?"
        if (type == TransactionType.INCOME) {
            contextType = Constants.INCOME_ATTACHMENT
        } else if (type == TransactionType.EXPENSE) {
            contextType = Constants.EXPENSE_ATTACHMENT
        }
        return contextType
    }
}

