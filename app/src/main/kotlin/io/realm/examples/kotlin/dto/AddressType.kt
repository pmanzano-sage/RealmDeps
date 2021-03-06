package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmAddressType

/**
 * Address Type model.
 * All parameters must have default values since a no argument constructor is required.
 *
 * BASIC ENTITY (no dependencies)
 */
data class AddressType(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String = "",
        val symbol: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmAddressType> {
        return RealmAddressType::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw InvalidFieldException("AddressType name can not be blank!\nOffending instance:\n${this}")
        }
        if (symbol.isBlank()) {
            throw InvalidFieldException("AddressType symbol can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmAddressType {
        return convertToDb(AddressType::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    // Convenient factory methods for V3
    companion object {

        enum class V3 {
            DELIVERY,
            ACCOUNTS,
            SALES,
            PURCHASING
        }

        fun create(type: V3): AddressType {
            return AddressType(type.name, SyncStatus.getDefault(), type.name.toLowerCase().replace('_', ' '), type.name)
        }
    }
}