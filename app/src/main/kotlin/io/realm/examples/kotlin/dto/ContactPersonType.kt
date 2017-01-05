package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId
import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmContactPersonType

/**
 * Contact Person Type model.
 *
 * BASIC ENTITY (no dependencies)
 */
data class ContactPersonType(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String = "",
        val symbol: String = ""
) : Dto {

    override fun getDbClass(): Class<out RealmContactPersonType> {
        return RealmContactPersonType::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("ContactPersonType name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmContactPersonType {
        return convertToDb(ContactPersonType::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    companion object {

        enum class V3 {
            ACCOUNTS,
            SALES,
            PURCHASING,
            CONTRACTOR,
            EMPLOYEE,
            OTHER
        }

        fun create(type: V3): ContactPersonType {
            return ContactPersonType(type.name, SyncStatus.getDefault(), type.name.toLowerCase().replace('_', ' '), type.name)
        }

        fun createList(types: List<V3>): List<ContactPersonType> {
            return types.map { create(it) }
        }
    }
}