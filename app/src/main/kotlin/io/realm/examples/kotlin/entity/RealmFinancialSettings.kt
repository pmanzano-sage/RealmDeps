package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.FinancialSettings
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmFinancialSettings(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var baseCurrency: String = "",
        open var taxScheme: RealmTaxScheme? = null,
        open var taxNumber: String? = null,
        open var taxSubmissionFrequencyType: String? = null,
        open var isTaxRegistered: Boolean = false
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmFinancialSettings::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out FinancialSettings> {
        return FinancialSettings::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmFinancialSettings::class.java, realm)
    }

}