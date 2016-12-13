package io.realm.examples.kotlin.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.Account
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDto
import io.realm.examples.kotlin.mapper.generateId
import java.util.*

open class RealmAccount(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var nominalCode: Int = 0,
        open var displayName: String? = null,
        open var number: String? = null,
        open var accountType: RealmAccountType = RealmAccountType(),
        open var balance: String? = null,
        open var currency: String = "",
        open var editable: Boolean = true
) : RealmObject(), Db {

    override fun toDto(): Dto {
        return convertToDto(RealmAccount::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        // TODO check this criteria
        return accountType.readyToSave()
    }

    override fun getDtoClass(): Class<out Account> {
        return Account::class.java
    }

}
