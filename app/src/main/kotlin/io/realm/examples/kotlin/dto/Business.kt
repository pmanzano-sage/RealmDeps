package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmBusiness
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Business model
 */
data class Business(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var name: String,
        var website: String,
        var telephone: String,
        var mobile: String,
        var address: Address
) : Dto {

    override fun getDbClass(): Class<out RealmBusiness> {
        return RealmBusiness::class.java
    }

    override fun checkValid(): Dto {
        if (name.isBlank()) {
            throw IllegalArgumentException("Business name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDb(): RealmBusiness {
        return convertToDb(Business::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }
}