package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Address
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmAddress(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var streetOne: String? = null,
        open var streetTwo: String? = null,
        open var town: String? = null,
        open var county: String? = null,
        open var postCode: String? = null,
        open var country: RealmCountry? = null
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmAddress::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Address> {
        return Address::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmAddress::class.java, realm)
    }

}
