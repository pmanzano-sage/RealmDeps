package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Payment
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmPayment(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var reference: String = "",
        open var amount: Double = 0.0,
        open var currencyCode: String = "",
        open var date: String = "",
        open var account: RealmAccount? = null,
        override var parentId: String = ""
) : RealmObject(), Db, DbChild {

    override fun toDto(): Dto {
        return convertToDto(RealmPayment::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Payment> {
        return Payment::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmPayment::class.java, realm)
    }

}
