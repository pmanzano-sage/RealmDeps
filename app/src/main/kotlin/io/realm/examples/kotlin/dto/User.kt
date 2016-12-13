package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmUser
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common User model.
 */
data class User(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var firstname: String,
        var surname: String,
        var email: String
) : Dto {

    override fun getDbClass(): Class<out RealmUser> {
        return RealmUser::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmUser {
        return convertToDb(User::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return "$firstname $surname"
    }
}