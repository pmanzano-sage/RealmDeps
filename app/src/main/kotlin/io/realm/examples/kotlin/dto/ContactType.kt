package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmContactType
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Contact Type model
 */
data class ContactType(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val name: String,
        val symbol: String
) : Dto {

    override fun getDbClass(): Class<out RealmContactType> {
        return RealmContactType::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmContactType {
        return convertToDb(ContactType::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }

    companion object {
        val SYMBOL_SUPPLIER = "SUPPLIER"
        val SYMBOL_CUSTOMER = "CUSTOMER"
    }
}