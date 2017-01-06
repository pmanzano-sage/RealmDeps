package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmPayment
import java.text.SimpleDateFormat

/**
 * A sales invoice payment. "parentId" will point to the invoice this Payment belongs to."
 */
data class Payment(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),

        val reference: String = "",
        val amount: Double = 0.0,
        val currencyCode: String = "",

        val date: String = "",
        var account: Account? = null,

        var parentId: String = "") : Dto {

    override fun getDbClass(): Class<out RealmPayment> {
        return RealmPayment::class.java
    }

    override fun checkValid(): Dto {
        if (reference.isBlank()) {
            throw InvalidFieldException("Payment reference can not be blank!\nOffending instance:\n${this}")
        }
        if (!Amount.isCurrencyCodeValid(currencyCode)) {
            throw InvalidFieldException("Payment currency code is not supported!\nOffending instance:\n${this}")
        }
        if (Math.abs(amount) < 0.0000001) {
            throw InvalidFieldException("RealmPayment amount can not be zero!\nOffending instance:\n${this}")
        }

        try {
            account?.checkValid()
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("Payment has invalid dependencies", e)
        }

        return this
    }

    override fun toDbModel(): RealmPayment {
        return convertToDb(Payment::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return reference
    }

    var timestamp: String = ""

    // Convenient factory methods
    companion object {
        @JvmField val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

        fun create(id: String, parentId: String, date: String, amount: Double, currencyCode: String, reference: String, account: Account): Payment {
            val (finalId, status) = Dto.init(id)
            return Payment(finalId, status, reference, amount, currencyCode, date, account, parentId)
        }

    }
}

