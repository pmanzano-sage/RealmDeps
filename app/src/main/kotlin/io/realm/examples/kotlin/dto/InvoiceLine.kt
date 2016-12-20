package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmInvoiceLine
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId

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
        var taxRate: TaxRate? = null,
        var totalAmount: Double = 0.0,
        var parentId: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmInvoiceLine> {
        return RealmInvoiceLine::class.java
    }

    override fun checkValid(): Dto {
        if (displayAs.isBlank()) {
            throw IllegalArgumentException("InvoiceLine displayAs can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmInvoiceLine {
        return convertToDb(InvoiceLine::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return displayAs
    }

}
