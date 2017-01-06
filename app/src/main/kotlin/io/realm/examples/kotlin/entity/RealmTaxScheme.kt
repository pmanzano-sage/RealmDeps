package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.TaxScheme
import io.realm.examples.kotlin.dto.definition.SyncStatus

@RealmClass
@SupportsIdOnly
open class RealmTaxScheme(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,

        open var name: String = ""
) : RealmDbModel {

    override fun toDto(): TaxScheme {
        return convertToDto(RealmTaxScheme::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw InvalidFieldException("RealmTaxScheme name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out TaxScheme> {
        return TaxScheme::class.java
    }

}
