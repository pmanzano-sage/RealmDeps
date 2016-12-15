package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.ContactType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

@RealmClass
open class RealmContactType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var symbol: String = "",
        open var name: String = ""
) : Db {

    override fun toDto(): Dto {
        return convertToDto(RealmContactType::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out ContactType> {
        return ContactType::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmContactType::class.java, realm)
    }

}
