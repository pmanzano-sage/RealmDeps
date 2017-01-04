package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus

/**
 * Common Amount model
 */
data class Amount(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val value: Double,
        val currencyCode: String
) : Dto {

    override fun toDbModel(): RealmDbModel {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getDbClass(): Class<out RealmDbModel> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun checkValid(): Dto {
        if (!isCurrencyCodeValid(currencyCode)) {
            throw IllegalArgumentException("Amount with invalid currency! \nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDisplayString(): String {
        return "$value"
    }

    constructor(value: Double, currencyCode: String) : this(generateId(), SyncStatus.getDefault(), value, currencyCode)

    // Convenient factory methods
    companion object {

        private val validCurrencies = arrayOf("GBP", "EUR", "USD")

        fun isCurrencyCodeValid(currencyCode: String): Boolean {
            return validCurrencies.contains(currencyCode)
        }

        fun pounds(amount: Double): Amount {
            return Amount(amount, "GBP")
        }

        fun euros(amount: Double): Amount {
            return Amount(amount, "EUR")
        }

        fun dollars(amount: Double): Amount {
            return Amount(amount, "USD")
        }
    }

}