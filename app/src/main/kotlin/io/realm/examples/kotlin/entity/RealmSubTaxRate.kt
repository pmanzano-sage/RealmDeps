package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.SubTaxRate
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.*
import java.util.*

open class RealmSubTaxRate(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),
        open var percentage: String = "",
        open var name: String = "",
        open var symbol: String = "",
        open var editable: Boolean = false,
        open var deletable: Boolean = false,
        open var parentApiId: String = ""
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmSubTaxRate::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return true
    }

    override fun getDtoClass(): Class<out SubTaxRate> {
        return SubTaxRate::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmSubTaxRate::class.java, realm)
    }

}
