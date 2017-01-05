package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTransaction
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

    override fun checkValid(): Dto {
        if (title!!.isBlank()) {
            throw InvalidFieldException("Transaction title can not be blank!\nOffending instance:\n${this}")
        }
        try {
            amount.checkValid()
            accountSource?.checkValid()
            accountDest?.checkValid()
            category?.checkValid()
            attachment?.checkValid()
            contact?.checkValid()
            taxRate?.checkValid()
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("Transaction has invalid dependencies", e)
        }
        return this
    }

    override fun toDbModel(): RealmTransaction {
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

