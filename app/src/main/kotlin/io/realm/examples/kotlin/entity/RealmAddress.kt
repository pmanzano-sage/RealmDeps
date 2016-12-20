package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Address
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
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
        open var country: RealmCountry? = null,
        var addressType: RealmAddressType? = null
) : DbModel {

    override fun toDto(): Address {
        return convertToDto(RealmAddress::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Address> {
        return Address::class.java
    }


}
