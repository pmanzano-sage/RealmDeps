package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.SalesInvoice
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

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
        open var invoiceLines: RealmList<RealmInvoiceLine>? = null,
        open var payments: RealmList<RealmPayment>? = null,
        open var contact: RealmContact? = null) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmSalesInvoice::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out SalesInvoice> {
        return SalesInvoice::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmSalesInvoice::class.java, realm)
    }

}

