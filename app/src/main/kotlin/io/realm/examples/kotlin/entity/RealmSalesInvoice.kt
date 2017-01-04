package io.realm.examples.kotlin.entity

import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.SalesInvoice
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmSalesInvoice(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var displayAs: String = "",
        open var totalAmount: Double = 0.0,
        open var netAmount: Double = 0.0,
        open var taxAmount: Double = 0.0,
        open var amountOutstanding: Double = 0.0,
        open var currencyCode: String = "",
        open var invoiceNumber: String = "",
        open var reference: String = "",
        open var date: String = "",
        open var dueDate: String = "",
        open var notes: String = "",
        open var termsAndConditions: String = "",

        @CascadeOnDelete
        open var invoiceLines: RealmList<RealmInvoiceLine>? = null,

        @CascadeOnDelete
        open var payments: RealmList<RealmPayment>? = null,

        open var contact: RealmContact? = null
) : RealmDbModel {

    override fun toDto(): SalesInvoice {
        return convertToDto(RealmSalesInvoice::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (displayAs.isBlank()) {
            throw IllegalArgumentException("RealmSalesInvoice displayAs can not be blank!\nOffending instance:\n${this}")
        }
        invoiceLines?.map { it.checkValid() }
        payments?.map { it.checkValid() }

        return this
    }

    override fun getDtoClass(): Class<out SalesInvoice> {
        return SalesInvoice::class.java
    }


}

