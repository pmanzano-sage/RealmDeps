package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmMe
import io.realm.examples.kotlin.mapper.Dto
import io.realm.examples.kotlin.mapper.convertToDb
import io.realm.examples.kotlin.mapper.generateId

/**
 * Common Me model
 */
data class Me(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        var serviceUids: List<String>
) : Dto {

    override fun getDbClass(): Class<out RealmMe> {
        return RealmMe::class.java
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun toDb(): RealmMe {
        return convertToDb(Me::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return serviceUids.joinToString()
    }
}