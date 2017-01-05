package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmContactPerson

/**
 * Contact Person model
 */
data class ContactPerson(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var contactPersonTypes: List<ContactPersonType>? = null,
        var name: String = "",
        var jobTitle: String = "",
        var telephone: String = "",
        var mobile: String = "",
        var email: String = "",
        var fax: String = "",
        var isMainContact: Boolean = false,
        var address: Address? = null
) : Dto {

    override fun getDbClass(): Class<out RealmContactPerson> {
        return RealmContactPerson::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw InvalidFieldException("ContactPerson name can not be blank!\nOffending instance:\n${this}")
        }
        try {
            contactPersonTypes?.map { it.checkValid() }
            address?.checkValid()
        } catch (e: InvalidFieldException) {
            throw InvalidDependencyException("ContactPerson has invalid dependencies", e)
        }
        return this
    }

    override fun toDbModel(): RealmContactPerson {
        return convertToDb(ContactPerson::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    // Convenient factory methods
    companion object {
        fun create(id: String?, types: List<ContactPersonType>, name: String, jobTitle: String, telephone: String, mobile: String, email: String, fax: String, address: Address?): ContactPerson {
            val (finalId, status) = Dto.init(id)
            return ContactPerson(finalId, status, types, name, jobTitle, telephone, mobile, email, fax, true, address)
        }
    }

}