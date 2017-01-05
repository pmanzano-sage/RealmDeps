package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmContactType
import java.util.*

/**
 * Common Contact Type model
 *
 * BASIC ENTITY (no dependencies)
 */
data class ContactType(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String = "",
        val symbol: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmContactType> {
        return RealmContactType::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("ContactType name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmContactType {
        return convertToDb(ContactType::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    // Convenient factory methods for V3
    companion object {

        enum class V3 {
            DELIVERY,
            ACCOUNTS,
            SALES,
            PURCHASING
        }

        fun create(type: V3): ContactType {
            return ContactType(type.name, SyncStatus.getDefault(), type.name.toLowerCase().replace('_', ' '), type.name)
        }

        fun createList(types: ArrayList<V3>): List<ContactType> {
            return types.map { ContactType.create(it) }
        }
    }
}