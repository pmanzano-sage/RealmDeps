package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.AccountType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
@SupportsIdOnly
open class RealmAccountType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var symbol: String = ""
) : RealmDbModel {

    override fun toDto(): AccountType {
        return convertToDto(RealmAccountType::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (name.isBlank()) {
            throw InvalidFieldException("RealmAccountType name can not be blank!\nOffending instance:\n${this}")
        }
        if (symbol.isBlank()) {
            throw InvalidFieldException("RealmAccountType symbol can not be blank!\nOffending instance:\n${this}")
        }
        return this

    }

    override fun getDtoClass(): Class<out AccountType> {
        return AccountType::class.java
    }

}
