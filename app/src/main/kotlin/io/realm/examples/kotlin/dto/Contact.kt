package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmContact

/**
 * Common Contact model
 */
data class Contact(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var contactTypes: List<ContactType>? = null,
        var name: String? = "",
        var reference: String? = "",
        var mainAddress: Address? = null,
        var deliveryAddress: Address? = null,
        var mainContactPerson: ContactPerson? = null
) : Dto {

    override fun getDbClass(): Class<out RealmContact> {
        return RealmContact::class.java
    }

    override fun checkValid(): Dto {
        if (name!!.isBlank()) {
            throw InvalidFieldException("Contact name can not be blank!\nOffending instance:\n${this}")
        }
        try {
            mainAddress?.checkValid()
            deliveryAddress?.checkValid()
            contactTypes?.map { it.checkValid() }
            mainContactPerson?.checkValid()

        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("Contact has invalid dependencies", e)
        }
        return this
    }

    override fun toDbModel(): RealmContact {
        return convertToDb(Contact::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name ?: ""
    }

    // Convenient factory methods for V3
    companion object {

        fun create(types: List<ContactType>, name: String, ref: String, mainAddress: Address?, deliveryAddress: Address?, mainContactPerson: ContactPerson?): Contact {
            return Contact(generateId(), SyncStatus.getDefault(), types, name, ref, mainAddress, deliveryAddress, mainContactPerson)
        }
    }
}