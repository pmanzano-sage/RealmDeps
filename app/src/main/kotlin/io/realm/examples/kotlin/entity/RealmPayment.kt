package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Payment
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.BackLink
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
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
) : DbModel, BackLink {

    override fun toDto(): Payment {
        return convertToDto(RealmPayment::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out Payment> {
        return Payment::class.java
    }

}
