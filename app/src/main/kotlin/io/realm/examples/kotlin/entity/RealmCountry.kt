package io.realm.examples.kotlin.entity

import io.realm.examples.kotlin.dto.Country
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDto
import io.realm.examples.kotlin.mapper.generateId
import java.util.*

open class RealmCountry(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var code: String = "",
        open var name: String = ""
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmCountry::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Country> {
        return Country::class.java
    }


}
