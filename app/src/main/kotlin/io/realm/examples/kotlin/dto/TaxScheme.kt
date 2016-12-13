package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTaxScheme
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Occurs inside a FinancialSettings entity.
 */
data class TaxScheme(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String
) : Dto {

    override fun getDbClass(): Class<out RealmTaxScheme> {
        return RealmTaxScheme::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmTaxScheme {
        return convertToDb(TaxScheme::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return name
    }
}