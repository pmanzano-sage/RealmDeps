package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.User
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmUser(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var firstname: String = "",
        open var lastname: String = "",
        open var email: String = ""
) : RealmDbModel {

    override fun toDto(): User {
        return convertToDto(RealmUser::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (email.isBlank()) {
            throw IllegalArgumentException("RealmUser email can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out User> {
        return User::class.java
    }


}
