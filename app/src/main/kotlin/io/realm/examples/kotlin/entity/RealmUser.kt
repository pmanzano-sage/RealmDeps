package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.User
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmUser(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var firstname: String = "",
        open var lastname: String = "",
        open var email: String = ""
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmUser::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out User> {
        return User::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmUser::class.java, realm)
    }

}
