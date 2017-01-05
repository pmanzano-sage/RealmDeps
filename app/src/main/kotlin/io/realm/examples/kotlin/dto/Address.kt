package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.Dto.Companion.init
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAddress

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
        var country: Country? = null,
        var addressType: AddressType? = null
) : Dto {

    override fun getDbClass(): Class<out RealmAddress> {
        return RealmAddress::class.java
    }

    override fun checkValid(): Dto {
        country?.checkValid()
        addressType?.checkValid()
        return this
    }

    override fun toDbModel(): RealmAddress {
        return convertToDb(Address::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return "$streetOne $streetTwo $town $country $postCode"
    }

    // Convenient factory methods
    companion object {

        /**
         * Example of usage:
         * Address.create( id, "street1", "street2", "town", "county", "postCode", AddressType.Companion.V3.DELIVERY )
         */
        fun create(
                id: String? = null,
                streetOne: String? = "",
                streetTwo: String? = "",
                town: String? = "",
                county: String? = "",
                postCode: String? = "",
                type: AddressType.Companion.V3, countryCode: Country.Companion.Code = Country.Companion.Code.UK): Address {

            val addressType = AddressType.create(type)
            val country = Country.create(countryCode)

            val (finalId, status) = init(id)

            return Address(
                    finalId,
                    status,
                    streetOne,
                    streetTwo,
                    town,
                    county,
                    postCode,
                    country,
                    addressType)
        }
    }

}