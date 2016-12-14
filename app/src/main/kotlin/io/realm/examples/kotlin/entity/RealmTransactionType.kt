package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.TransactionType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmTransactionType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var ordinal: Int = 0
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmTransactionType::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out TransactionType> {
        return TransactionType::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmTransactionType::class.java, realm)
    }

}
