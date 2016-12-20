package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Country
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
open class RealmCountry(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var code: String = "",
        open var name: String = ""
) : DbModel {

    override fun toDto(): Country {
        return convertToDto(RealmCountry::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Country> {
        return Country::class.java
    }


}
