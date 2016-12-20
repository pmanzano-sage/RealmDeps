package io.realm.examples.kotlin.entity

import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.ContactPerson
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
open class RealmContactPerson(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var contactPersonTypes: RealmList<RealmContactPersonType>? = null,
        open var name: String = "",
        open var jobTitle: String = "",
        open var telephone: String = "",
        open var mobile: String = "",
        open var email: String = "",
        open var fax: String = "",
        open var isMainContact: Boolean = false,
        open var address: RealmAddress? = null
) : DbModel {

    override fun toDto(): ContactPerson {
        return convertToDto(RealmContactPerson::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out ContactPerson> {
        return ContactPerson::class.java
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
