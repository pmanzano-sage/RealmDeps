package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Business
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmBusiness(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var website: String = "",
        open var telephone: String = "",
        open var mobile: String = "",
        open var street1: String = "",
        open var street2: String = "",
        open var city: String = "",
        open var county: String = "",
        open var postcode: String = "",
        open var countryName: String = "",
        open var countryCode: String? = ""
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmBusiness::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Business> {
        return Business::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmBusiness::class.java, realm)
    }

}
