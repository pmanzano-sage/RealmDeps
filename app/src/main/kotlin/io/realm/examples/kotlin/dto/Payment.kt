package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmPayment
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId
import java.text.SimpleDateFormat

/**
 * A sales invoice payment. "parentId" will point to the invoice this Payment belongs to."
 */
data class Payment(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),

        val reference: String = "",

        val amount: Double,

        val currencyCode: String,

        val date: String,
        var account: Account? = null,

        var parentId: String = "") : Dto {

    override fun getDbClass(): Class<out RealmPayment> {
        return RealmPayment::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmPayment {
        return convertToDb(Payment::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return reference
    }

    var timestamp: String = ""

    companion object {
        @JvmField val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
    }
}

