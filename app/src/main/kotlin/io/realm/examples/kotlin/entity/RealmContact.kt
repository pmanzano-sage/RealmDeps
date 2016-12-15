package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Contact
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

@RealmClass
open class RealmContact(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var emailAddress: String = "",
        open var phoneNumber: String = "",
        open var phoneNumber2: String = "",
        open var photoUri: String = "",
        open var sourceId: String = "",
        open var address: RealmAddress? = null,
        open var company: String = "",
        open var contactType: RealmContactType? = null
) : Db {

    override fun toDto(): Dto {
        return convertToDto(RealmContact::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Contact> {
        return Contact::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmContact::class.java, realm)
    }

    companion object {
        @JvmField
        val FIELD_DISPLAY_NAME = "name"
        @JvmStatic
        val FIELD_CREATION_DATE = "creationDate"
        @JvmStatic
        val FIELD_UPDATE_DATE = "updateDate"
    }
}
