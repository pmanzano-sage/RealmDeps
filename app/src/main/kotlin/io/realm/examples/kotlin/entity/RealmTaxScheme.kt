package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.TaxScheme
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId

@RealmClass
open class RealmTaxScheme(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,

        open var name: String = ""
) : DbModel {

    override fun toDto(): TaxScheme {
        return convertToDto(RealmTaxScheme::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out TaxScheme> {
        return TaxScheme::class.java
    }

}
