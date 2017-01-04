package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.FinancialSettings
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
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
) : RealmDbModel {

    override fun toDto(): FinancialSettings {
        return convertToDto(RealmFinancialSettings::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        return this
    }

    override fun getDtoClass(): Class<out FinancialSettings> {
        return FinancialSettings::class.java
    }

}
