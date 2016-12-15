package io.realm.examples.kotlin.entity

import io.realm.Realm
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import io.realm.examples.kotlin.dto.AccountType
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.mapper.Db
import io.realm.examples.kotlin.mapper.convertToDto
import io.realm.examples.kotlin.mapper.deleteCascade
import io.realm.examples.kotlin.mapper.generateId
import java.util.*

@RealmClass
open class RealmAccountType(
        @PrimaryKey @Required override var id: String = generateId(),
        override var sync: Int = SyncStatus.getDefault().ordinal,
        open var creationDate: Date = Date(),
        open var updateDate: Date = Date(),

        open var name: String = "",
        open var symbol: String? = ""
) : Db {

    override fun toDto(): AccountType {
        return convertToDto(RealmAccountType::class.java, getDtoClass())
    }

    override fun readyToSave(): Boolean {
        return name.isNotEmpty()
    }

    override fun getDtoClass(): Class<out AccountType> {
        return AccountType::class.java
    }

    override fun delete(realm: Realm): Boolean {
        return deleteCascade(RealmAccountType::class.java, realm)
    }

}
