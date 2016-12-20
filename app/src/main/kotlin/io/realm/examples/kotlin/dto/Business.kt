package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmBusiness
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId

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

    override fun toDbModel(): RealmBusiness {
        return convertToDb(Business::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name
    }
}