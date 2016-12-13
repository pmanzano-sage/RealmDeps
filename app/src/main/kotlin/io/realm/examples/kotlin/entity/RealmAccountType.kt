package io.realm.examples.kotlin.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.AccountType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDto
import io.realm.examples.kotlin.mapper.generateId
import java.util.*

open class RealmAccountType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var symbol: String? = ""
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmAccountType::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return name.isNotEmpty()
    }

    override fun getDtoClass(): Class<out AccountType> {
        return AccountType::class.java
    }


}
