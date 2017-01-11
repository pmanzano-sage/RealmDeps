package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmInvoiceLine

/**
 * @author Pablo Manzano
 * @since 24/11/16
 */
data class InvoiceLine(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var displayAs: String = "",
        var quantity: Double = 0.0,
        var unitPrice: Double = 0.0,
        var netAmount: Double = 0.0,
        var taxAmount: Double = 0.0,
        var category: TransactionCategory? = null,
        var taxRate: TaxRate? = null,
        var totalAmount: Double = 0.0,
        var parentId: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmInvoiceLine> {
        return RealmInvoiceLine::class.java
    }

    override fun checkValid(): Dto {
        if (displayAs.isBlank()) {
            throw InvalidFieldException("InvoiceLine displayAs can not be blank!\nOffending instance:\n${this}")
        }
        if (Math.abs(quantity) < 0.0000001) {
            throw InvalidFieldException("InvoiceLine quantity can not be zero!\nOffending instance:\n${this}")
        }

        try {
            taxRate?.checkValid()
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("RealmInvoiceLine has invalid dependencies", e)
        }
        return this
    }

    override fun toDbModel(): RealmInvoiceLine {
        return convertToDb(InvoiceLine::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return displayAs
    }

    // Convenient factory methods
    companion object {

        fun create(id: String, parentId: String, qty: Double, price: Double, description: String, taxRate: TaxRate): InvoiceLine {
            val taxPercentage = taxRate.percentage.toDouble()
            val total = qty * price
            val netPercentage = 1 - taxPercentage
            val net = total * netPercentage
            val tax = total * taxPercentage

            val (finalId, status) = Dto.init(id)

            return InvoiceLine(finalId, status, description, qty, price, net, tax, null, taxRate, total, parentId)
        }

    }

}
