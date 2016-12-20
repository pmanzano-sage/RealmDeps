package io.realm.examples.kotlin.entity

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.AddressType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.data.DbModel
import io.realm.examples.kotlin.data.convertToDto
import io.realm.examples.kotlin.data.generateId
import java.util.*

@RealmClass
open class RealmAddressType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var symbol: String? = ""
) : DbModel {

    override fun toDto(): AddressType {
        return convertToDto(RealmAddressType::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return name.isNotEmpty()
    }

    override fun getDtoClass(): Class<out AddressType> {
        return AddressType::class.java
    }


}
