package io.realm.examples.kotlin.entity

import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Contact
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.CascadeOnDelete
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
open class RealmContact(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var contactTypes: RealmList<RealmContactType>? = null,
        open var name: String = "",
        open var reference: String = "",

        @CascadeOnDelete
        open var mainAddress: RealmAddress? = null,

        @CascadeOnDelete
        open var deliveryAddress: RealmAddress? = null,

        // Do not cascade since this person may be shared
        open var mainContactPerson: RealmContactPerson? = null
) : DbModel {

    override fun toDto(): Contact {
        return convertToDto(RealmContact::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Contact> {
        return Contact::class.java
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
