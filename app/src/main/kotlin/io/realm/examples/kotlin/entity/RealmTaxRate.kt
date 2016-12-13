package io.realm.examples.kotlin.entity

import io.realm.examples.kotlin.dto.TaxRate
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDto
import io.realm.examples.kotlin.mapper.generateId
import java.util.*

open class RealmTaxRate(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var percentage: String = "",
        open var name: String = "",
        open var symbol: String = "",
        open var isCurrent: Boolean = false,
        open var editable: Boolean = false,
        open var deletable: Boolean = false,
        open var subTaxRates: RealmList<RealmSubTaxRate>? = null
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmTaxRate::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out TaxRate> {
        return TaxRate::class.java
    }

}
