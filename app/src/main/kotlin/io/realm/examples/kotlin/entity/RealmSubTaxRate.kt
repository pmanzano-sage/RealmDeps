package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.SubTaxRate
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmSubTaxRate(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),
        open var percentage: String = "",
        open var name: String = "",
        open var symbol: String = "",
        open var editable: Boolean = false,
        open var deletable: Boolean = false,
        open var parentApiId: String = ""
) : RealmDbModel {

    override fun toDto(): SubTaxRate {
        return convertToDto(RealmSubTaxRate::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw InvalidFieldException("RealmSubTaxRate name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out SubTaxRate> {
        return SubTaxRate::class.java
    }


}
