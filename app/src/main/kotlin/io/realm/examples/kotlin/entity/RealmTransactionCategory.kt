package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.TransactionCategory
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmTransactionCategory(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var moneyIn: Boolean = false,
        open var nominalCode: Int = 0,
        open var label: String = "",
        open var taxCodeId: String = ""
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmTransactionCategory::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out TransactionCategory> {
        return TransactionCategory::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmTransactionCategory::class.java, realm)
    }

    companion object {
        val FIELD_NAME = "label"
    }
}
