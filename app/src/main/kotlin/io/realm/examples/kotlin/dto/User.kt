package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.InvalidFieldException
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmUser

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

    override fun checkValid(): Dto {
        if (email.isBlank()) {
            throw InvalidFieldException("User email can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmUser {
        return convertToDb(User::class.java, getDbClass())
    }


    override fun toDisplayString(): String {
        return "$firstname $surname"
    }
}