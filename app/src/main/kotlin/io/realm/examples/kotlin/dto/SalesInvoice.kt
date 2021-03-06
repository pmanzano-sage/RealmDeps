package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmSalesInvoice
import org.joda.time.LocalDate
import java.util.*

/**
 * Common Sales Invoice model
 */
data class SalesInvoice(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),

        val displayAs: String = "",

        val totalAmount: Double = 0.0,
        val netAmount: Double = 0.0,
        val taxAmount: Double = 0.0,
        val amountOutstanding: Double = 0.0,

        val currencyCode: String = "",

        val invoiceNumber: String = "",
        val reference: String = "",
        val date: String = Transaction.dateFormatter.format(SalesInvoice.now),
        val dueDate: String = SalesInvoice.dueDateFromDate(SalesInvoice.now),

        val notes: String = "",
        val termsAndConditions: String = "",

        var invoiceLines: List<InvoiceLine>? = null,
        var payments: List<Payment>? = null,
        val contact: Contact? = null) : Dto {

    override fun getDbClass(): Class<out RealmSalesInvoice> {
        return RealmSalesInvoice::class.java
    }

    override fun checkValid(): Dto {
        if (displayAs.isBlank()) {
            throw InvalidFieldException("SalesInvoice displayAs can not be blank!\nOffending instance:\n${this}")
        }
        try {
            invoiceLines?.map { it.checkValid() }
            payments?.map { it.checkValid() }
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("SalesInvoice has invalid dependencies", e)
        }
        return this
    }

    override fun toDbModel(): RealmSalesInvoice {
        return convertToDb(SalesInvoice::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return displayAs
    }

    companion object {
        val now = Date()
        val DueDateDefaultPeriod = 30

        fun dueDateFromDate(date: Date): String {
            return Transaction.dateFormatter.format(LocalDate(date).plusDays(DueDateDefaultPeriod).toDate())
        }
    }
}

