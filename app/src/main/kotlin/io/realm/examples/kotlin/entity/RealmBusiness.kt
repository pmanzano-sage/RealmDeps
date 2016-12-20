package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Business
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
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
) : DbModel {

    override fun toDto(): Business {
        return convertToDto(RealmBusiness::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Business> {
        return Business::class.java
    }


}
