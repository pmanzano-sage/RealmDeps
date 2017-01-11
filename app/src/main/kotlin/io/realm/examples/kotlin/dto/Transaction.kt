package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidDependencyException
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTransaction
import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * Common Transaction model
 */
data class Transaction(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var amount: Amount = Amount.pounds(0.0),
        val title: String = "",
        val reference: String = "",
        val date: String = "",
        var accountSource: Account? = null,
        var accountDest: Account? = null,
        val type: Int = 0,
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
        if (title.isBlank()) {
            throw InvalidFieldException("Transaction title can not be blank!\nOffending instance:\n${this}")
            // mandatory or need validation
            // amount > 0, title, type
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

//    override fun toDbModel(): RealmTransaction {
//        return convertToDb(Transaction::class.java, getDbClass())
//    }

    // Custom mapper because of amount
    override fun toDbModel(): RealmTransaction {
        return RealmTransaction(
                id,
                sync.ordinal,
                amount = DECIMAL_FORMATTER.format(amount.value),
                currency = amount.currencyCode,
                title = title,
                reference = reference,
                date = date,
                accountSource = accountSource?.toDbModel(),
                accountDest = accountDest?.toDbModel(),
                category = category?.toDbModel(),
                taxRate = taxRate?.toDbModel(),
                contact = contact?.toDbModel(),
                attachment = attachment?.toDbModel(),
                isReadonly = readonly,
                timestamp = timestamp
        )
    }

    override fun toDisplayString(): String {
        return title
    }

    var timestamp: String = ""

    companion object {
        @JvmField val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        private val DECIMAL_FORMATTER = DecimalFormat.getCurrencyInstance()

        fun createMoneyIn(id: String? = null, title: String, reference: String, amount: Amount, date: String, accountDest: Account, contact: Contact? = null, taxRate: TaxRate? = null): Transaction {
            val (finalId, status) = Dto.init(id)
            val transactionCategory = TransactionCategory.create(TransactionCategory.Companion.V3.OTHER_PAYMENT)
            return Transaction(finalId, status, amount, title, reference, date, null, accountDest, TransactionType.INCOME, transactionCategory, null, contact, taxRate, false)
        }

        fun createMoneyOut(id: String? = null, title: String, reference: String, amount: Amount, date: String, accountSource: Account): Transaction {
            val (finalId, status) = Dto.init(id)
            val transactionCategory = TransactionCategory.create(TransactionCategory.Companion.V3.OTHER_PAYMENT)
            return Transaction(finalId, status, amount, title, reference, date, accountSource, null, TransactionType.EXPENSE, transactionCategory, null, null, null, false)
        }

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

    fun numAccounts(): Int {
        return arrayListOf(accountSource, accountDest).filterNotNull().count()
    }
}

