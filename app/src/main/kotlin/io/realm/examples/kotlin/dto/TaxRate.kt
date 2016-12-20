package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTaxRate
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId

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
            throw IllegalArgumentException("TaxRate name can not be blank!\nOffending instance:\n${this}")
        }
        subTaxRates?.map { it.checkValid() }
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

        if (!id.equals(other.id)) return false
        if (!symbol.equals(other.symbol)) return false
        if (!name.equals(other.name)) return false
        if (!percentage.equals(other.percentage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + percentage.hashCode()
        return result
    }

}