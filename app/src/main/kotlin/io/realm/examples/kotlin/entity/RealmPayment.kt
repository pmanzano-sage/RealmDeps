package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.Amount
import io.realm.examples.kotlin.dto.Payment
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmPayment(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var reference: String = "",
        open var amount: Double = 0.0,
        open var currencyCode: String = "",
        open var date: String = "",
        open var account: RealmAccount? = null,
        override var parentId: String = ""
) : RealmDbModel, BackLink {

    override fun toDto(): Payment {
        return convertToDto(RealmPayment::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (reference.isBlank()) {
            throw InvalidFieldException("RealmPayment reference can not be blank!\nOffending instance:\n${this}")
        }
        if (!Amount.isCurrencyCodeValid(currencyCode)) {
            throw InvalidFieldException("RealmPayment currency code is not supported!\nOffending instance:\n${this}")
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

    override fun getDtoClass(): Class<out Payment> {
        return Payment::class.java
    }

}
