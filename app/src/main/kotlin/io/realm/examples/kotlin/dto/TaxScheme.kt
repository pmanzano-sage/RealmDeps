package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmTaxScheme
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId

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

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("TaxScheme name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmTaxScheme {
        return convertToDb(TaxScheme::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return name
    }
}