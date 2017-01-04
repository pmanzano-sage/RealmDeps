package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.AddressType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
@SupportsIdOnly
open class RealmAddressType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var symbol: String = ""
) : RealmDbModel {

    override fun toDto(): AddressType {
        return convertToDto(RealmAddressType::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw IllegalArgumentException("RealmAddressType name can not be blank!\nOffending instance:\n${this}")
        }
        if (symbol.isBlank()) {
            throw IllegalArgumentException("RealmAddressType symbol can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out AddressType> {
        return AddressType::class.java
    }


}
