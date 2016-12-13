package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmCountry
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Country model
 */
data class Country(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val code: String?,
        val name: String?
) : Dto {

    override fun getDbClass(): Class<out RealmCountry> {
        return RealmCountry::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmCountry {
        return convertToDb(Country::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name ?: ""
    }
}