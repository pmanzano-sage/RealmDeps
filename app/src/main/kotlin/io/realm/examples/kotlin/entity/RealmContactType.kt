package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.ContactType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
@SupportsIdOnly
open class RealmContactType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var symbol: String = "",
        open var name: String = ""
) : RealmDbModel {

    override fun toDto(): ContactType {
        return convertToDto(RealmContactType::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw InvalidFieldException("RealmContactType name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out ContactType> {
        return ContactType::class.java
    }


}
