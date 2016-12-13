package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.Constants
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmContact
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Contact model
 */
data class Contact(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var name: String? = "",
        var emailAddress: String? = "",
        var phoneNumber: String = "",
        var phoneNumber2: String = "",
        var photoUri: String? = null,
        var address: Address? = null,
        var sourceId: String? = "",
        var company: String? = "",
        var contactType: ContactType
) : Dto {

    override fun getDbClass(): Class<out RealmContact> {
        return RealmContact::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmContact {
        return convertToDb(Contact::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name ?: ""
    }

    fun fixCompanyName() {
        if (!company.isNullOrEmpty() && company.equals(Constants.CONTACTS_COMPANY_DEFAULT)) {
            company = ""
        }
    }
}