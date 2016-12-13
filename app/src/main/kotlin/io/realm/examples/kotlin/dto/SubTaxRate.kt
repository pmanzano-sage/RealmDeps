package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmSubTaxRate
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Sub Tax Rate model
 */
data class SubTaxRate(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String,
        val symbol: String,
        val percentage: String,
        val current: Boolean,
        val editable: Boolean? = false,
        val deletable: Boolean? = false,
        var parentApiId: String
) : Dto {

    override fun getDbClass(): Class<out RealmSubTaxRate> {
        return RealmSubTaxRate::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmSubTaxRate {
        return convertToDb(SubTaxRate::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return name
    }
}