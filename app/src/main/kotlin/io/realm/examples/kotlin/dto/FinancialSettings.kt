package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmFinancialSettings

/**
 * Common Financial Settings model
 *
 * For the moment the most important (or used) fields are baseCurrency and taxRegistered.
 */
data class FinancialSettings(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val baseCurrency: String,
        val taxNumber: String,
        val taxSubmissionFrequencyType: String,
        val taxScheme: TaxScheme,
        val taxRegistered: Boolean
) : Dto {

    override fun getDbClass(): Class<out RealmFinancialSettings> {
        return RealmFinancialSettings::class.java
    }

    override fun checkValid(): Dto {
        // TODO
        return this
    }

    override fun toDbModel(): RealmFinancialSettings {
        return convertToDb(FinancialSettings::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return ""
    }
}