package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.*
import io.realm.examples.kotlin.dto.Country
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
@SupportsIdOnly
open class RealmCountry(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var code: String = "",
        open var name: String = ""
) : RealmDbModel {

    override fun toDto(): Country {
        return convertToDto(RealmCountry::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        try {
            Country.Companion.Code.valueOf(code)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unsupported country used! \nOffending instance:\n${this}")
        }
        if (name.isBlank()) {
            throw IllegalArgumentException("RealmCountry name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out Country> {
        return Country::class.java
    }


}
