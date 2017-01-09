package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmSubTaxRate

/**
 * Common Sub Tax Rate model.
 *
 * BASIC ENTITY (no dependencies)
 */
data class SubTaxRate(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String = "",
        val symbol: String = "",
        val percentage: String = "",
        val current: Boolean = true,
        val editable: Boolean? = false,
        val deletable: Boolean? = false,
        var parentId: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmSubTaxRate> {
        return RealmSubTaxRate::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw InvalidFieldException("SubTaxRate name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmSubTaxRate {
        return convertToDb(SubTaxRate::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return name
    }

    // Convenient factory methods
    companion object {

        /**
         * Example of usage:
         * val subTaxRate = SubTaxRate.create( id, parentId, "NoTax", "0.0", true )
         */
        fun create(id: String, parentId: String, name: String, percentage: String, current: Boolean): SubTaxRate {
            val (finalId, status) = Dto.init(id)
            return SubTaxRate(finalId, status, name, name.toUpperCase(), percentage, current, false, false, parentId)
        }

    }

}