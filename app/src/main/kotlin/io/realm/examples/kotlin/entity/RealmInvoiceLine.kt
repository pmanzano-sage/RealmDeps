package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.InvoiceLine
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmInvoiceLine(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var displayAs: String = "",
        open var quantity: Double = 0.0,
        open var unitPrice: Double = 0.0,
        open var netAmount: Double = 0.0,
        open var taxAmount: Double = 0.0,
        open var taxRate: RealmTaxRate? = null,
        open var totalAmount: Double = 0.0,
        override var parentId: String = ""
) : RealmObject(), Db, DbChild {

    override fun toDto(): Dto {
        return convertToDto(RealmInvoiceLine::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out InvoiceLine> {
        return InvoiceLine::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmInvoiceLine::class.java, realm)
    }

    // Convenient factory methods
    companion object {

        private val DEFAULT_TAX = 0.1

        /**
         * Create an invoice line with a default tax.
         */
        fun create(parentId: String, qty: Double?, price: Double?, description: String): RealmInvoiceLine {
            val total = qty!! * price!!
            val netPercentage = 1 - DEFAULT_TAX
            val net = total * netPercentage!!
            val tax = total * DEFAULT_TAX
            return RealmInvoiceLine(
                    displayAs = description,
                    quantity = qty,
                    unitPrice = price,
                    netAmount = net,
                    taxAmount = tax,
                    taxRate = null,
                    totalAmount = total,
                    parentId = parentId)
        }

    }

}
