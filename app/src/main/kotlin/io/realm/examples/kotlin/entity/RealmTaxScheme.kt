package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.TaxScheme
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*

@RealmClass
open class RealmTaxScheme(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,

        open var name: String = ""
) : Db {

    override fun toDto(): Dto {
        return convertToDto(RealmTaxScheme::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out TaxScheme> {
        return TaxScheme::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmTaxScheme::class.java, realm)
    }

}
