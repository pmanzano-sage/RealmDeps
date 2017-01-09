package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTaxRate

/**
 * Common Tax Rate model
 */
data class TaxRate(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String = "",
        val symbol: String = "",
        val percentage: String = "",
        val current: Boolean = true,
        val editable: Boolean? = false,
        val deletable: Boolean? = false,
        var subTaxRates: List<SubTaxRate>? = null
) : Dto {

    override fun getDbClass(): Class<out RealmTaxRate> {
        return RealmTaxRate::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw InvalidFieldException("TaxRate name can not be blank!\nOffending instance:\n${this}")
        }
        try {
            subTaxRates?.map { it.checkValid() }
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("TaxRate has invalid dependencies", e)
        }
        return this
    }

    override fun toDbModel(): RealmTaxRate {
        return convertToDb(TaxRate::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TaxRate) return false

        if (id != other.id) return false
        if (symbol != other.symbol) return false
        if (name != other.name) return false
        if (percentage != other.percentage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + percentage.hashCode()
        return result
    }

    // Convenient factory methods
    companion object {

        /**
         * Example of usage:
         * val taxRate = TaxRate.create( id, "NoTax", "0.0", true )
         */
        fun create(id: String, name: String, percentage: String, current: Boolean, subTaxes: List<SubTaxRate>?): TaxRate {
            val (finalId, status) = Dto.init(id)
            return TaxRate(finalId, status, name, name.toUpperCase(), percentage, current, subTaxRates = subTaxes)
        }

    }

}
