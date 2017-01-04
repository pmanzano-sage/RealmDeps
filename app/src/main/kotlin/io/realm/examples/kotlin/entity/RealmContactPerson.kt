package io.realm.examples.kotlin.entity

import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.ContactPerson
import io.realm.examples.kotlin.dto.definition.SyncStatus
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
) : RealmDbModel {

    override fun toDto(): ContactPerson {
        return convertToDto(RealmContactPerson::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw IllegalArgumentException("RealmContactPerson name can not be blank!\nOffending instance:\n${this}")
        }
        contactPersonTypes?.map { it.checkValid() }
        address?.checkValid()
        return this
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
