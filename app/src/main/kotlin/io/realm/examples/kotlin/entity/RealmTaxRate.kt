package io.realm.examples.kotlin.entity

import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.TaxRate
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
@SupportsIdOnly
open class RealmTaxRate(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var percentage: String = "",
        open var name: String = "",
        open var symbol: String = "",
        open var current: Boolean = false,
        open var editable: Boolean = false,
        open var deletable: Boolean = false,

        @CascadeOnDelete
        open var subTaxRates: RealmList<RealmSubTaxRate>? = null

) : RealmDbModel {

    override fun toDto(): TaxRate {
        return convertToDto(RealmTaxRate::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw InvalidFieldException("RealmTaxRate name can not be blank!\nOffending instance:\n${this}")
        }
        try {
            subTaxRates?.map { it.checkValid() }
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("RealmTaxRate has invalid dependencies", e)
        }
        return this
    }

    override fun getDtoClass(): Class<out TaxRate> {
        return TaxRate::class.java
    }

}
