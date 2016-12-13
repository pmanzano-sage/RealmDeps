package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.Country
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAddress
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Address model
 */
data class Address(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var streetOne: String? = "",
        var streetTwo: String? = "",
        var town: String? = "",
        var county: String? = "",
        var postCode: String? = "",
        var country: Country? = null
) : Dto {

    override fun getDbClass(): Class<out RealmAddress> {
        return RealmAddress::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmAddress {
        return convertToDb(Address::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return "$streetOne $streetTwo $town $country $postCode"
    }
}