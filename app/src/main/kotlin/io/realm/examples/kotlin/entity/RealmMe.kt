package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.RealmDbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.Me
import io.realm.examples.kotlin.dto.definition.SyncStatus
import java.util.*

@RealmClass
open class RealmMe(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var serviceUids: String = ""
) : RealmDbModel {

    override fun toDto(): Me {
        return convertToDto(RealmMe::class.java, getDtoClass())
    }

    override fun checkValid(): DbModel {
        if (serviceUids.isEmpty()) {
            throw IllegalArgumentException("RealmMe has no services!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun getDtoClass(): Class<out Me> {
        return Me::class.java
    }

}
