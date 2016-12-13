package io.realm.examples.kotlin.entity

import io.realm.examples.kotlin.dto.InvoiceLine
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDto
import io.realm.examples.kotlin.mapper.generateId
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
        open var parentId: String = ""
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmInvoiceLine::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out InvoiceLine> {
        return InvoiceLine::class.java
    }

}
